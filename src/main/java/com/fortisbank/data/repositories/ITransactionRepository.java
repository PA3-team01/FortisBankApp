package com.fortisbank.data.repositories;

import com.fortisbank.exceptions.TransactionRepositoryException;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface for transaction repository operations.
 * Provides methods to manage transaction data.
 */
public interface ITransactionRepository {

    /**
     * Retrieves a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to retrieve
     * @return the transaction with the specified number, or null if not found
     */
    Transaction getTransactionByNumber(String transactionNumber) throws TransactionRepositoryException;

    /**
     * Retrieves all transactions associated with a specific account ID.
     *
     * @param accountId the ID of the account whose transactions to retrieve
     * @return a list of transactions associated with the specified account ID
     */
    TransactionList getTransactionsByAccount(String accountId) throws TransactionRepositoryException;

    /**
     * Retrieves all transactions.
     *
     * @return a list of all transactions
     */
    TransactionList getAllTransactions() throws TransactionRepositoryException;

    /**
     * Inserts a new transaction.
     *
     * @param transaction the transaction to insert
     */
    void insertTransaction(Transaction transaction) throws TransactionRepositoryException;

    /**
     * Deletes a transaction by its number.
     *
     * @param transactionNumber the number of the transaction to delete
     */
    void deleteTransaction(String transactionNumber) throws TransactionRepositoryException;

    /**
     * Retrieves transactions for a specific customer within a date range.
     *
     * @param customerID the ID of the customer whose transactions to retrieve
     * @param start the start date of the date range
     * @param end the end date of the date range
     * @return a list of transactions for the specified customer within the date range
     */
    TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) throws TransactionRepositoryException;

    /**
     * Retrieves the balance for a specific customer before a given date.
     *
     * @param customerID the ID of the customer whose balance to retrieve
     * @param start the date before which to calculate the balance
     * @return the balance for the specified customer before the given date
     */
    BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) throws TransactionRepositoryException;
}