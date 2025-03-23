package com.fortisbank.data.repositories;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.transactions.Transaction;
import java.util.List;

public interface IAccountRepository {
    Account getAccountById(String accountId);
    AccountList getAccountsByCustomerId(String customerId);
    AccountList getAllAccounts();
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(String accountId);

    void recordTransaction(Transaction transaction);
    List<Transaction> getTransactionsForAccount(String accountId);
}
