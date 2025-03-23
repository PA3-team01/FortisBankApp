package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

public class TransactionRepositoryFile implements ITransactionRepository {
    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        return null;
    }

    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        return null;
    }

    @Override
    public TransactionList getAllTransactions() {
        return null;
    }

    @Override
    public void insertTransaction(Transaction transaction) {

    }

    @Override
    public void deleteTransaction(String transactionNumber) {

    }
}
