package com.fortisbank.business.services;

import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.ITransactionRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.exceptions.InvalidTransactionException;
import com.fortisbank.models.accounts.*;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * Service de gestion des transactions
 */
public class TransactionService implements ITransactionService {

    private static final Map<StorageMode, TransactionService> instances = new EnumMap<>(StorageMode.class);
    private final ITransactionRepository transactionRepository;
    private final IAccountRepository accountRepository;
    private final NotificationService notificationService = NotificationService.getInstance();

    private TransactionService(StorageMode storageMode) {
        var factory = RepositoryFactory.getInstance(storageMode);
        this.transactionRepository = factory.getTransactionRepository();
        this.accountRepository = factory.getAccountRepository();
    }

    public static synchronized TransactionService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, TransactionService::new);
    }

    // ---------------------------------------------------------------------------------------
    // CORE BUSINESS METHOD
    // ---------------------------------------------------------------------------------------

    /**
     * Fait une transaction selon le type
     * @param transaction Transaction
     * throw InvalidTransactionException si la transaction est invalide
     */
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
                transaction.setSourceAccount(destination); // Set destination as source
                adjustBalance(destination, amount);
                destination.addTransaction(transaction);
                accountRepository.updateAccount(destination);

                if (destination.getCustomer() != null) {
                    notificationService.notifyTransactionReceipt(destination.getCustomer(), transaction);
                }
            }

            case WITHDRAWAL -> {
                validateNotNull(source, "Source account");
                transaction.setDestinationAccount(source); // Set source as destination
                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);

                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                applyTransactionFeeIfRequired(source);
                accountRepository.updateAccount(source);

                if (source.getCustomer() != null) {
                    notificationService.notifyTransactionReceipt(source.getCustomer(), transaction);
                }
            }

            case TRANSFER -> {
                validateNotNull(source, "Source account");
                validateNotNull(destination, "Destination account");

                if (source.getAccountNumber().equals(destination.getAccountNumber())) {
                    throw new InvalidTransactionException("Cannot transfer to the same account.");
                }

                validateCreditLimit(source, amount);
                validateSufficientFunds(source, amount);

                adjustBalance(source, amount.negate());
                adjustBalance(destination, amount);

                source.addTransaction(transaction);
                destination.addTransaction(transaction);

                applyTransactionFeeIfRequired(source);

                accountRepository.updateAccount(source);
                accountRepository.updateAccount(destination);

                if (source.getCustomer() != null) {
                    notificationService.notifyTransactionReceipt(source.getCustomer(), transaction);
                }
                if (destination.getCustomer() != null) {
                    notificationService.notifyTransactionReceipt(destination.getCustomer(), transaction);
                }
            }

            case FEE -> {
                validateNotNull(source, "Source account");
                transaction.setDestinationAccount(source); // Set source as destination
                validateSufficientFunds(source, amount);

                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                accountRepository.updateAccount(source);
            }

            default -> throw new InvalidTransactionException("Unsupported transaction type.");
        }

        transactionRepository.insertTransaction(transaction);
    }



    // ---------------------------------------------------------------------------------------
    // INTEREST & CURRENCY OPERATIONS
    // ---------------------------------------------------------------------------------------

    /**
     * Applique les interets mensuels sur compte credit
     * @param account Compte
     */
    public void applyInterestToCreditAccount(CreditAccount account) {
        BigDecimal rate = account.getInterestRate();
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal interest = account.getAvailableBalance().multiply(rate);
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            applyFee(account, interest, "Monthly interest applied.");
        }
    }

    /**
     * Applique les interets annuels sur saving account
     * @param account Compte
     */
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
    }

    /**
     * Filtre les transactions recentes
     * @param transactions Transactions
     * @param days Jours
     * @return les transactions filtrees
     */
    public TransactionList filterRecentTransactions(TransactionList transactions, int days) {
        Date startDate = new Date(System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000);
        Date endDate = new Date();

        if (startDate.after(endDate)) {
            throw new InvalidTransactionException("Invalid date range: start date cannot be after end date.");
        }

        return transactions.filterByDateRange(startDate, endDate);
    }

    /**
     * Recupere les transactions recentes du compte
     * @param account Compte
     * @param days Jours
     * @return les transactions recentes
     */
    //helper method to get recent transaction by account
    public TransactionList getRecentTransactionsByAccount(Account account, int days) {
        TransactionList transactions = transactionRepository.getTransactionsByAccount(account.getAccountNumber());
        return filterRecentTransactions(transactions, days);
    }

    // ---------------------------------------------------------------------------------------
    // REPOSITORY WRAPPERS
    // ---------------------------------------------------------------------------------------
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

    // ---------------------------------------------------------------------------------------
    // VALIDATION HELPERS
    // ---------------------------------------------------------------------------------------

    /**
     * Valide qu'un objet n'est pas null
     * @param obj Objet
     * @param fieldName
     */
    private void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new InvalidTransactionException(fieldName + " cannot be null.");
        }
    }

    /**
     * Verifie si le compte a asser de fonds
     * @param account Compte
     * @param amount Montant
     */
    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (!account.hasSufficientFunds(amount)) {
            throw new InvalidTransactionException("Insufficient funds in account: " + account.getAccountNumber());
        }
    }

    /**
     * Verifie si le withdraw amount respecte la limite de credit
     * @param account Compte
     * @param withdrawalAmount Montant a retirer
     */
    private void validateCreditLimit(Account account, BigDecimal withdrawalAmount) {
        if (account instanceof CreditAccount creditAccount) {
            BigDecimal totalAvailable = creditAccount.getAvailableBalance().add(creditAccount.getCreditLimit());
            if (totalAvailable.compareTo(withdrawalAmount) < 0) {
                throw new InvalidTransactionException("Withdrawal exceeds credit limit.");
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // INTERNAL OPERATIONS
    // ---------------------------------------------------------------------------------------

    /**
     * Ajuste le solde du compte
     * @param account Compte
     * @param delta
     */
    private void adjustBalance(Account account, BigDecimal delta) {
        BigDecimal updated = account.getAvailableBalance().add(delta);
        account.setAvailableBalance(updated);
    }

    /**
     * Applique les frais de transaction si necessaire
     * @param account Compte
     */
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

    /**
     * Applique les frais au compte
     * @param account Compte
     * @param feeAmount Montant frais
     * @param description
     */
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
}
