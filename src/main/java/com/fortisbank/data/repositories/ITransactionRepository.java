package com.fortisbank.data.repositories;

import com.fortisbank.models.Transaction;
import java.util.List;

public interface ITransactionRepository {
    Transaction getTransactionById(String transactionId);
    List<Transaction> getTransactionsByAccountId(String accountId);
    List<Transaction> getAllTransactions();
    void insertTransaction(Transaction transaction);
    void deleteTransaction(String transactionId);
}
