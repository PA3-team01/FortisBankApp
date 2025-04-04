package com.fortisbank.business.services.transaction;

import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.services.notification.NotificationService;
import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.ITransactionRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.exceptions.InvalidTransactionException;
import com.fortisbank.models.accounts.*;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.others.NotificationType;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class TransactionService implements ITransactionService {

    private static final Map<StorageMode, TransactionService> instances = new EnumMap<>(StorageMode.class);
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final StorageMode storageMode;
    private final NotificationService notificationService;

    private TransactionService(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.notificationService = NotificationService.getInstance(storageMode);
        var factory = RepositoryFactory.getInstance(storageMode);
        this.transactionRepository = factory.getTransactionRepository();
        this.accountRepository = factory.getAccountRepository();
    }

    public static synchronized TransactionService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, TransactionService::new);
    }

    public void executeTransaction(Transaction transaction) {
        ValidationUtils.validateNotNull(transaction, "Transaction");
        ValidationUtils.validateAmount(transaction.getAmount());

        Account source = transaction.getSourceAccount();
        Account destination = transaction.getDestinationAccount();
        BigDecimal amount = transaction.getAmount();
        TransactionType type = transaction.getTransactionType();

        switch (type) {
            case DEPOSIT:
                validateNotNull(destination, "Destination account");
                adjustBalance(destination, amount);
                destination.addTransaction(transaction);
                accountRepository.updateAccount(destination);
                break;

            case WITHDRAWAL:
                validateNotNull(source, "Source account");
                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                applyTransactionFeeIfRequired(source);
                accountRepository.updateAccount(source);
                break;

            case TRANSFER:
                validateNotNull(source, "Source account");
                validateNotNull(destination, "Destination account");
                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                adjustBalance(destination, amount);
                source.addTransaction(transaction);
                destination.addTransaction(transaction);
                applyTransactionFeeIfRequired(source);
                accountRepository.updateAccount(source);
                accountRepository.updateAccount(destination);
                break;

            case FEE:
                validateNotNull(source, "Source account");
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                accountRepository.updateAccount(source);
                break;

            default:
                throw new InvalidTransactionException("Unsupported transaction type.");
        }

        transactionRepository.insertTransaction(transaction);
    }

    public void applyInterestToCreditAccount(CreditAccount account) {
        BigDecimal rate = account.getInterestRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal interest = account.getAvailableBalance().multiply(rate);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            applyFee(account, interest, "Monthly interest applied.");
        }
        notificationService.sendNotification(
                account.getCustomer(),
                NotificationType.INFO,
                "Monthly Interest Charged",
                String.format("An interest charge of $%.2f has been applied to your credit account (%s).",
                        interest, account.getAccountNumber()),
                account.getCustomer(),
                account
        );
    }

    public void applyAnnualInterestToSavingsAccount(SavingsAccount account) {
        BigDecimal rate = account.getAnnualInterestRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal interest = account.getAvailableBalance().multiply(rate);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            Transaction tx = TransactionFactory.createTransaction(
                    TransactionType.DEPOSIT,
                    "Annual interest applied",
                    new Date(),
                    interest,
                    null,
                    account
            );

            adjustBalance(account, interest);
            account.addTransaction(tx);
            transactionRepository.insertTransaction(tx);
            accountRepository.updateAccount(account);
        }
        notificationService.sendNotification(
                account.getCustomer(),
                NotificationType.INFO,
                "Annual Interest Credited",
                String.format("An interest of $%.2f has been credited to your savings account (%s).",
                        interest, account.getAccountNumber()),
                account.getCustomer(),
                account
        );
    }

    public TransactionList filterRecentTransactions(TransactionList transactions, int days) {
        Date startDate = new Date(System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000);
        Date endDate = new Date();

        if (startDate.after(endDate)) {
            throw new InvalidTransactionException("Invalid date range: start date cannot be after end date.");
        }

        return transactions.filterByDateRange(startDate, endDate);
    }

    @Override
    public void createTransaction(Transaction transaction) {
        transactionRepository.insertTransaction(transaction);
    }

    @Override
    public void deleteTransaction(String transactionNumber) {
        transactionRepository.deleteTransaction(transactionNumber);
    }

    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        return transactionRepository.getTransactionByNumber(transactionNumber);
    }

    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        return transactionRepository.getTransactionsByAccount(accountId);
    }

    @Override
    public TransactionList getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) {
        return transactionRepository.getTransactionsByCustomerAndDateRange(customerID, start, end);
    }

    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) {
        return transactionRepository.getBalanceBeforeDate(customerID, start);
    }

    private void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new InvalidTransactionException(fieldName + " cannot be null.");
        }
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (!account.hasSufficientFunds(amount)) {
            throw new InvalidTransactionException("Insufficient funds in account: " + account.getAccountNumber());
        }
    }

    private void validateCreditLimit(Account account, BigDecimal withdrawalAmount) {
        if (account instanceof CreditAccount creditAccount) {
            BigDecimal totalAvailable = creditAccount.getAvailableBalance().add(creditAccount.getCreditLimit());
            if (totalAvailable.compareTo(withdrawalAmount) < 0) {
                throw new InvalidTransactionException("Withdrawal exceeds credit limit.");
            }
        }
    }

    private void adjustBalance(Account account, BigDecimal delta) {
        BigDecimal updated = account.getAvailableBalance().add(delta);
        account.setAvailableBalance(updated);
    }

    private void applyTransactionFeeIfRequired(Account account) {
        if (account.getAccountType() != AccountType.CHECKING) return;

        TransactionList transactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());
        long transactionCountThisMonth = transactions
                .filterByMonth(LocalDate.now())
                .filterByTypes(TransactionType.WITHDRAWAL, TransactionType.TRANSFER)
                .size();

        if (transactionCountThisMonth >= CheckingAccount.FREE_TRANSACTION_LIMIT) {
            applyFee(account, CheckingAccount.TRANSACTION_FEE,
                    "Transaction fee after " + CheckingAccount.FREE_TRANSACTION_LIMIT + " free transactions.");
        }
    }

    private void applyFee(Account account, BigDecimal feeAmount, String description) {
        validateSufficientFunds(account, feeAmount);

        Transaction feeTx = TransactionFactory.createTransaction(
                TransactionType.FEE,
                description,
                new Date(),
                feeAmount,
                account,
                null
        );

        adjustBalance(account, feeAmount.negate());
        account.addTransaction(feeTx);
        transactionRepository.insertTransaction(feeTx);
        accountRepository.updateAccount(account);
    }

    public TransactionList getRecentTransactionsByAccount(Account account) {
        return transactionRepository.getTransactionsByAccount(account.getAccountNumber());
    }

    public void applyMonthlyInterestToAllCreditAccounts() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        var accountService = AccountService.getInstance(storageMode);

        for (var customer : customerRepo.getAllCustomers()) {
            var accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            for (var account : accounts) {
                if (account instanceof CreditAccount creditAccount && creditAccount.isEligibleForInterestCalculation()) {
                    applyInterestToCreditAccount(creditAccount);
                    creditAccount.setLastInterestApplied(LocalDate.now());
                    accountRepository.updateAccount(creditAccount);
                }
            }
        }
    }

    public void applyAnnualInterestToAllSavingsAccounts() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        var accountService = AccountService.getInstance(storageMode);

        for (var customer : customerRepo.getAllCustomers()) {
            var accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            for (var account : accounts) {
                if (account instanceof SavingsAccount savingsAccount && savingsAccount.isEligibleForInterestCalculation()) {
                    applyAnnualInterestToSavingsAccount(savingsAccount);
                    savingsAccount.setLastInterestApplied(LocalDate.now());
                    accountRepository.updateAccount(savingsAccount);
                }
            }
        }
    }

    public void scanForSuspiciousActivity() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        var accountService = AccountService.getInstance(storageMode);
        var notificationService = NotificationService.getInstance(storageMode);
        BigDecimal suspiciousAmount = new BigDecimal("5000");

        for (var customer : customerRepo.getAllCustomers()) {
            var accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            for (var account : accounts) {
                var recentTransactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());

                for (Transaction tx : recentTransactions) {
                    if ((tx.getTransactionType() == TransactionType.WITHDRAWAL ||
                            tx.getTransactionType() == TransactionType.TRANSFER)
                            && tx.getAmount().compareTo(suspiciousAmount) >= 0) {

                        notificationService.sendNotification(
                                customer,
                                NotificationType.SECURITY_ALERT,
                                "Unusual Transaction Detected",
                                "A high-value " + tx.getTransactionType().name().toLowerCase()
                                        + " of $" + tx.getAmount() + " occurred on account " + account.getAccountNumber(),
                                customer,
                                account
                        );

                        break;
                    }
                }

                var recent = recentTransactions.stream()
                        .filter(t -> t.getTransactionDate().after(new Date(System.currentTimeMillis() - 60000)))
                        .count();

                if (recent > 10) {
                    notificationService.sendNotification(
                            customer,
                            NotificationType.SECURITY_ALERT,
                            "Suspicious Activity",
                            "More than 10 transactions were made on account " + account.getAccountNumber()
                                    + " within a minute.",
                            customer,
                            account
                    );
                }
            }
        }
    }
}