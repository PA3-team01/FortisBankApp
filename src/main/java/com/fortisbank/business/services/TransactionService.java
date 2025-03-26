package com.fortisbank.business.services;

import com.fortisbank.exceptions.InvalidTransactionException;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.utils.ValidationUtils;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionService {
    public void executeTransaction(Transaction transaction) {

        ValidationUtils.validateNotNull(transaction, "Transaction");
        ValidationUtils.validateAmount(transaction.getAmount());

        Account source = transaction.getSourceAccount();
        Account destination = transaction.getDestinationAccount();

        //Check if sufficient funds
        if (source != null && source.getAvailableBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient funds to complete the transaction.");
        }
        if (destination == null) {
            throw new InvalidTransactionException("Destination account cannot be null.");
        }

        try {
            transaction.processTransaction();
        } catch (Exception e) {
            throw new InvalidTransactionException("Error processing the transaction: " + e.getMessage());
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
}
