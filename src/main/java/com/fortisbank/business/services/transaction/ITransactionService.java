package com.fortisbank.business.services.transaction;

import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface for transaction-related operations.
 */
public interface ITransactionService {

    /**
     * Creates a new transaction.
     *
     * @param transaction the transaction to be created
     */
    void createTransaction(Transaction transaction);

    /**
     * Deletes a transaction by its transaction number.
     *
     * @param transactionNumber the transaction number of the transaction to be deleted
     */
    void deleteTransaction(String transactionNumber);

    /**
     * Retrieves a transaction by its transaction number.
     *
     * @param transactionNumber the transaction number of the transaction to be retrieved
     * @return the transaction with the specified transaction number
     */
    Transaction getTransactionByNumber(String transactionNumber);

    /**
     * Retrieves all transactions for a specific account.
     *
     * @param accountId the ID of the account
     * @return the list of transactions for the specified account
     */
    TransactionList getTransactionsByAccount(String accountId);

    /**
     * Retrieves all transactions.
     *
     * @return the list of all transactions
     */
    TransactionList getAllTransactions();

    /**
     * Retrieves transactions for a specific customer within a date range.
     *
     * @param customerID the ID of the customer
     * @param start the start date of the range
     * @param end the end date of the range
     * @return the list of transactions for the specified customer within the date range
     */
    TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end);

    /**
     * Retrieves the balance of a customer before a specific date.
     *
     * @param customerID the ID of the customer
     * @param start the date before which the balance is to be retrieved
     * @return the balance of the customer before the specified date
     */
    BigDecimal getBalanceBeforeDate(String customerID, LocalDate start);
}