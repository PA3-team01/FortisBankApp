package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Repository class for managing transactions in a file.
 * Implements the ITransactionRepository interface.
 */
public class TransactionRepositoryFile extends FileRepository<Transaction> implements ITransactionRepository {
    private static final File file = new File("data/transactions.ser");
    private static TransactionRepositoryFile instance;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private TransactionRepositoryFile() {
        super(file);
    }

    /**
     * Returns the singleton instance of TransactionRepositoryFile.
     * Synchronized to ensure thread safety.
     *
     * @return the singleton instance
     */
    public static synchronized TransactionRepositoryFile getInstance() {
        if (instance == null) {
            instance = new TransactionRepositoryFile();
        }
        return instance;
    }

    /**
     * Retrieves a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to retrieve
     * @return the transaction with the specified number, or null if not found
     */
    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        return readAll().stream()
                .filter(t -> t.getTransactionNumber().equals(transactionNumber))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all transactions associated with a specific account ID.
     *
     * @param accountId the ID of the account whose transactions to retrieve
     * @return a list of transactions associated with the specified account ID
     */
    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        return readAll().stream()
                .filter(t -> {
                    Account sourceAccount = t.getSourceAccount();
                    return (sourceAccount != null && sourceAccount.getAccountNumber().equals(accountId))
                            || (t.getDestinationAccount() != null
                            && t.getDestinationAccount().getAccountNumber().equals(accountId));
                })
                .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
    }

    /**
     * Retrieves all transactions.
     *
     * @return a list of all transactions
     */
    @Override
    public TransactionList getAllTransactions() {
        return new TransactionList(readAll());
    }

    /**
     * Inserts a new transaction into the file.
     *
     * @param transaction the transaction to insert
     */
    @Override
    public void insertTransaction(Transaction transaction) {
        var transactions = readAll();
        transactions.add(transaction);
        writeAll(transactions);
    }

    /**
     * Deletes a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to delete
     */
    @Override
    public void deleteTransaction(String transactionNumber) {
        var transactions = readAll();
        transactions.removeIf(t -> t.getTransactionNumber().equals(transactionNumber));
        writeAll(transactions);
    }

    /**
     * Retrieves transactions for a specific customer within a date range.
     *
     * @param customerID the ID of the customer whose transactions to retrieve
     * @param start the start date of the date range
     * @param end the end date of the date range
     * @return a list of transactions for the specified customer within the date range
     */
    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) {
        ZoneId zone = ZoneId.systemDefault();
        return readAll().stream()
                .filter(t -> {
                    Account sourceAccount = t.getSourceAccount();
                    if (sourceAccount == null) {
                        return false;
                    }
                    String transactionCustomerID = sourceAccount.getCustomer().getUserId();
                    LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                    return transactionCustomerID.equals(customerID) &&
                            (!transactionDate.isBefore(start) && !transactionDate.isAfter(end));
                })
                .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
    }

    /**
     * Retrieves the balance for a specific customer before a given date.
     *
     * @param customerID the ID of the customer whose balance to retrieve
     * @param start the date before which to calculate the balance
     * @return the balance for the specified customer before the given date
     */
    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) {
        ZoneId zone = ZoneId.systemDefault();
        return readAll().stream()
                .filter(t -> {
                    String transactionCustomerID = t.getSourceAccount().getCustomer().getUserId();
                    LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                    return transactionCustomerID.equals(customerID) && transactionDate.isBefore(start);
                })
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}