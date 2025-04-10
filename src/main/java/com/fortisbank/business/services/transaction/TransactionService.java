package com.fortisbank.business.services.transaction;

import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.services.notification.NotificationService;
import com.fortisbank.contracts.exceptions.InvalidTransactionException;
import com.fortisbank.contracts.exceptions.TransactionRepositoryException;
import com.fortisbank.contracts.models.accounts.*;
import com.fortisbank.contracts.collections.TransactionList;
import com.fortisbank.contracts.models.others.NotificationType;
import com.fortisbank.contracts.models.transactions.*;
import com.fortisbank.contracts.utils.ValidationUtils;
import com.fortisbank.data.dal_utils.RepositoryFactory;
import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.data.interfaces.ITransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling all transaction operations.
 */
public class TransactionService implements ITransactionService {

    private static final Map<StorageMode, TransactionService> instances = new EnumMap<>(StorageMode.class);

    private final ITransactionRepository transactionRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;
    private final StorageMode storageMode;

    private TransactionService(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.notificationService = NotificationService.getInstance(storageMode);
        var factory = RepositoryFactory.getInstance(storageMode);
        this.transactionRepository = factory.getTransactionRepository();
        this.accountService = AccountService.getInstance(storageMode);
    }

    public static synchronized TransactionService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, TransactionService::new);
    }

    @Override
    public void createTransaction(Transaction transaction) {  // should not be used directly
        try {
            transactionRepository.insertTransaction(transaction);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to create transaction", e);
        }
    }

    @Override
    public void deleteTransaction(String transactionNumber) { // should not be used
        try {
            transactionRepository.deleteTransaction(transactionNumber);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to delete transaction with number: " + transactionNumber, e);
        }
    }

    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        try {
            return transactionRepository.getTransactionByNumber(transactionNumber);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve transaction with number: " + transactionNumber, e);
        }
    }

    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        try {
            return transactionRepository.getTransactionsByAccount(accountId);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve transactions for account: " + accountId, e);
        }
    }

    @Override
    public TransactionList getAllTransactions() {
        try {
            return transactionRepository.getAllTransactions();
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve all transactions", e);
        }
    }

    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) {
        try {
            return transactionRepository.getTransactionsByCustomerAndDateRange(customerID, start, end);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve transactions for customer: " + customerID + " within date range", e);
        }
    }

    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) {
        try {
            return transactionRepository.getBalanceBeforeDate(customerID, start);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve balance for customer: " + customerID + " before date: " + start, e);
        }
    }

    public void executeTransaction(Transaction transaction) {
        ValidationUtils.validateNotNull(transaction, "Transaction");
        ValidationUtils.validateAmount(transaction.getAmount());

        Account source = transaction.getSourceAccount();
        Account destination = transaction.getDestinationAccount();
        BigDecimal amount = transaction.getAmount();
        TransactionType type = transaction.getTransactionType();

        switch (type) {
            case DEPOSIT -> {
                validateNotNull(destination, "Destination account");
                adjustBalance(destination, amount);
                destination.addTransaction(transaction);
                accountService.updateAccount(destination);
            }
            case WITHDRAWAL -> {
                validateNotNull(source, "Source account");
                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                applyTransactionFeeIfRequired(source);
                accountService.updateAccount(source);
            }
            case TRANSFER -> {
                validateNotNull(source, "Source account");
                validateNotNull(destination, "Destination account");
                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                adjustBalance(destination, amount);
                source.addTransaction(transaction);
                destination.addTransaction(transaction);
                applyTransactionFeeIfRequired(source);
                accountService.updateAccount(source);
                accountService.updateAccount(destination);
            }
            case FEE -> {
                validateNotNull(source, "Source account");
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                accountService.updateAccount(source);
            }
            default -> throw new InvalidTransactionException("Unsupported transaction type.");
        }

        try {
            transactionRepository.insertTransaction(transaction);
        } catch (TransactionRepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void applyInterestToCreditAccount(CreditAccount account) {
        BigDecimal rate = account.getInterestRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal interest = account.getAvailableBalance().multiply(rate);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            applyFee(account, interest, "Monthly interest applied.");
            notificationService.sendNotification(
                    account.getCustomer(), NotificationType.INFO,
                    "Monthly Interest Charged",
                    String.format("An interest charge of $%.2f has been applied to your credit account (%s).",
                            interest, account.getAccountNumber()),
                    account.getCustomer(), account);
        }
    }

    public void applyAnnualInterestToSavingsAccount(SavingsAccount account) {
        BigDecimal rate = account.getAnnualInterestRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal interest = account.getAvailableBalance().multiply(rate);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            Transaction tx = TransactionFactory.createTransaction(
                    TransactionType.DEPOSIT, "Annual interest applied", new Date(), interest, null, account);

            adjustBalance(account, interest);
            account.addTransaction(tx);
            try {
                transactionRepository.insertTransaction(tx);
            } catch (TransactionRepositoryException e) {
                throw new RuntimeException(e);
            }
            accountService.updateAccount(account);
            notificationService.sendNotification(
                    account.getCustomer(), NotificationType.INFO,
                    "Annual Interest Credited",
                    String.format("An interest of $%.2f has been credited to your savings account (%s).",
                            interest, account.getAccountNumber()),
                    account.getCustomer(), account);
        }
    }

    public TransactionList filterRecentTransactions(TransactionList transactions, int days) {
        Date startDate = new Date(System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000);
        Date endDate = new Date();

        if (startDate.after(endDate)) {
            throw new InvalidTransactionException("Invalid date range: start date cannot be after end date.");
        }

        return transactions.filterByDateRange(startDate, endDate);
    }

    public TransactionList getRecentTransactionsByAccount(Account account) {
        try {
            TransactionList transactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());
            return transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                    .limit(20)
                    .collect(Collectors.toCollection(TransactionList::new));
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to retrieve recent transactions for account: " + account.getAccountNumber(), e);
        }
    }

    public void applyMonthlyInterestToAllCreditAccounts() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        for (var customer : customerRepo.getAllCustomers()) {
            for (var account : accountService.getAccountsByCustomerId(customer.getUserId())) {
                if (account instanceof CreditAccount creditAccount && creditAccount.isEligibleForInterestCalculation()) {
                    applyInterestToCreditAccount(creditAccount);
                    creditAccount.setLastInterestApplied(LocalDate.now());
                    accountService.updateAccount(creditAccount);
                }
            }
        }
    }

    public void applyAnnualInterestToAllSavingsAccounts() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        for (var customer : customerRepo.getAllCustomers()) {
            for (var account : accountService.getAccountsByCustomerId(customer.getUserId())) {
                if (account instanceof SavingsAccount savingsAccount && savingsAccount.isEligibleForInterestCalculation()) {
                    applyAnnualInterestToSavingsAccount(savingsAccount);
                    savingsAccount.setLastInterestApplied(LocalDate.now());
                    accountService.updateAccount(savingsAccount);
                }
            }
        }
    }

    public void scanForSuspiciousActivity() {
        var customerRepo = RepositoryFactory.getInstance(storageMode).getCustomerRepository();
        BigDecimal suspiciousAmount = new BigDecimal("5000");

        for (var customer : customerRepo.getAllCustomers()) {
            for (var account : accountService.getAccountsByCustomerId(customer.getUserId())) {
                TransactionList transactions;
                try {
                    transactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());
                } catch (TransactionRepositoryException e) {
                    throw new ServiceException("Failed to retrieve transactions for account: " + account.getAccountNumber(), e);
                }

                for (Transaction tx : transactions) {
                    if ((tx.getTransactionType() == TransactionType.WITHDRAWAL ||
                            tx.getTransactionType() == TransactionType.TRANSFER)
                            && tx.getAmount().compareTo(suspiciousAmount) >= 0) {

                        notificationService.sendNotification(
                                customer, NotificationType.SECURITY_ALERT,
                                "Unusual Transaction Detected",
                                String.format("A high-value %s of $%s occurred on account %s",
                                        tx.getTransactionType().name().toLowerCase(), tx.getAmount(), account.getAccountNumber()),
                                customer, account);
                        break;
                    }
                }

                long recentCount = transactions.stream()
                        .filter(t -> t.getTransactionDate().after(new Date(System.currentTimeMillis() - 60000)))
                        .count();

                if (recentCount > 10) {
                    notificationService.sendNotification(
                            customer, NotificationType.SECURITY_ALERT,
                            "Suspicious Activity",
                            String.format("More than 10 transactions were made on account %s within a minute.",
                                    account.getAccountNumber()),
                            customer, account);
                }
            }
        }
    }

    private void validateNotNull(Object obj, String fieldName) {
        if (obj == null) throw new InvalidTransactionException(fieldName + " cannot be null.");
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
        account.setAvailableBalance(account.getAvailableBalance().add(delta));
    }

    private void applyTransactionFeeIfRequired(Account account) {
        if (account.getAccountType() != AccountType.CHECKING) return;

        try {
            var transactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());
            long count = transactions.filterByMonth(LocalDate.now())
                    .filterByTypes(TransactionType.WITHDRAWAL, TransactionType.TRANSFER)
                    .size();

            if (count >= CheckingAccount.FREE_TRANSACTION_LIMIT) {
                applyFee(account, CheckingAccount.TRANSACTION_FEE,
                        "Transaction fee after " + CheckingAccount.FREE_TRANSACTION_LIMIT + " free transactions.");
            }
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to apply transaction fee", e);
        }
    }

    private void applyFee(Account account, BigDecimal feeAmount, String description) {
        validateSufficientFunds(account, feeAmount);

        Transaction feeTx = TransactionFactory.createTransaction(
                TransactionType.FEE, description, new Date(), feeAmount, account, null);

        adjustBalance(account, feeAmount.negate());
        account.addTransaction(feeTx);
        try {
            transactionRepository.insertTransaction(feeTx);
        } catch (TransactionRepositoryException e) {
            throw new ServiceException("Failed to insert transaction fee", e);
        }
        accountService.updateAccount(account);
    }
}
