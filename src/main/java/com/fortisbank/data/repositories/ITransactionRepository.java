package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ITransactionRepository {
    Transaction getTransactionByNumber(String transactionNumber);

    TransactionList getTransactionsByAccount(String accountId);

    TransactionList getAllTransactions();

    void insertTransaction(Transaction transaction);

    void deleteTransaction(String transactionNumber);

    TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end);

    BigDecimal getBalanceBeforeDate(String customerID, LocalDate start);
}

