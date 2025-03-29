package com.fortisbank.business.services;

import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ITransactionService {
    void createTransaction(Transaction transaction);
    void deleteTransaction(String transactionNumber);
    Transaction getTransactionByNumber(String transactionNumber);
    TransactionList getTransactionsByAccount(String accountId);
    TransactionList getAllTransactions();
    TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end);
    BigDecimal getBalanceBeforeDate(String customerID, LocalDate start);
}