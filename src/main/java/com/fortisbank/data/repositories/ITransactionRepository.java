package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import java.util.List;

public interface ITransactionRepository {
    Transaction getTransactionByNumber(String transactionNumber);
    TransactionList getTransactionsByAccount(String accountId);
    TransactionList getAllTransactions();
    void insertTransaction(Transaction transaction);
    void deleteTransaction(String transactionNumber);
}
