package com.fortisbank.data.repositories;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import java.util.List;

public interface IAccountRepository {
    Account getAccountById(String accountId);
    List<Account> getAccountsByCustomerId(String customerId);
    List<Account> getAllAccounts();
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(String accountId);

    void recordTransaction(Transaction transaction);
    List<Transaction> getTransactionsForAccount(String accountId);
}
