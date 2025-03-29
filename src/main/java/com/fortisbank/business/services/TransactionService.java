package com.fortisbank.business.services;

import com.fortisbank.data.repositories.ITransactionRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.exceptions.InvalidTransactionException;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
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

    private TransactionService(StorageMode storageMode) {
        this.transactionRepository = RepositoryFactory.getInstance(storageMode).getTransactionRepository();
    }

    public static synchronized TransactionService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, TransactionService::new);
    }

    // Core API for safely processing and saving a transaction
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
                break;

            case WITHDRAWAL:
                validateNotNull(source, "Source account");
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                break;

            case TRANSFER:
                validateNotNull(source, "Source account");
                validateNotNull(destination, "Destination account");
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                adjustBalance(destination, amount);
                source.addTransaction(transaction);
                destination.addTransaction(transaction);
                break;

            case FEE:
                validateNotNull(source, "Source account");
                validateSufficientFunds(source, amount);
                adjustBalance(source, amount.negate());
                source.addTransaction(transaction);
                break;

            default:
                throw new InvalidTransactionException("Unsupported transaction type.");
        }

        transactionRepository.insertTransaction(transaction);
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

    private void adjustBalance(Account account, BigDecimal delta) {
        BigDecimal updated = account.getAvailableBalance().add(delta);
        account.setAvailableBalance(updated);
    }

    // Repository passthrough methods
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

    public TransactionList filterRecentTransactions(TransactionList transactions, int days) {
        Date startDate = new Date(System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000);
        Date endDate = new Date();

        if (startDate.after(endDate)) {
            throw new InvalidTransactionException("Invalid date range: start date cannot be after end date.");
        }

        return transactions.filterByDateRange(startDate, endDate);
    }
}
