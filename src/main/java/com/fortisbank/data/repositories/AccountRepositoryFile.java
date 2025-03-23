package com.fortisbank.data.repositories;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.transactions.Transaction;

import java.util.List;

public class AccountRepositoryFile implements IAccountRepository {
    @Override
    public Account getAccountById(String accountId) {
        return null;
    }

    @Override
    public AccountList getAccountsByCustomerId(String customerId) {
        return null;
    }

    @Override
    public AccountList getAllAccounts() {
        return null;
    }

    @Override
    public void insertAccount(Account account) {

    }

    @Override
    public void updateAccount(Account account) {

    }

    @Override
    public void deleteAccount(String accountId) {

    }

    @Override
    public void recordTransaction(Transaction transaction) {

    }

    @Override
    public List<Transaction> getTransactionsForAccount(String accountId) {
        return List.of();
    }
}
