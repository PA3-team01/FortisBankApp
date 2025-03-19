package com.fortisbank.data.repositories;

import com.fortisbank.models.Account;
import java.util.List;

public interface IAccountRepository {
    Account getAccountById(String accountId);
    List<Account> getAccountsByCustomerId(String customerId);
    List<Account> getAllAccounts();
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(String accountId);
}
