package com.fortisbank.data.repositories;

import com.fortisbank.models.Transaction;
import java.util.List;

public interface ITransactionRepository {
    Transaction getTransactionByNumber(String transactionNumber);
    List<Transaction> getTransactionsByAccount(String accountId);
    List<Transaction> getAllTransactions();
    void insertTransaction(Transaction transaction);
    void deleteTransaction(String transactionNumber);
}
