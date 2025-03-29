package com.fortisbank.business.services;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

public interface IAccountService {
    void createAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(String accountId);
    Account getAccount(String accountId);
    AccountList getAccountsByCustomerId(String customerId);
    AccountList getAllAccounts();
}
