package com.fortisbank.business.services;

import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.AccountFactory;
import com.fortisbank.models.accounts.AccountType;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;


import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * AccountService manages account-related operations (CRUD) using the selected storage mode.
 * This service is now focused solely on accounts and no longer handles transactions.
 */
public class AccountService implements IAccountService {
    private static final Map<StorageMode, AccountService> instances = new EnumMap<>(StorageMode.class);
    public final IAccountRepository accountRepository;

    private AccountService(StorageMode storageMode) {
        this.accountRepository = RepositoryFactory.getInstance(storageMode).getAccountRepository();
    }

    public static synchronized AccountService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, AccountService::new);
    }

    @Override
    public void createAccount(Account account) {
        accountRepository.insertAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        accountRepository.updateAccount(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        accountRepository.deleteAccount(accountId);
    }

    @Override
    public Account getAccount(String accountId) {
        return accountRepository.getAccountById(accountId);
    }

    @Override
    public AccountList getAccountsByCustomerId(String customerId) {
        return accountRepository.getAccountsByCustomerId(customerId);
    }

    @Override
    public AccountList getAllAccounts() {
        return accountRepository.getAllAccounts();
    }
    /**
     * Creates and persists a default checking account for a new customer using the factory.
     * The account is initialized with zero balance and marked active by default.
     *
     * @param customer the customer for whom to create the account
     * @return the created Account instance
     */
    public Account createDefaultCheckingAccountFor(Customer customer) {
        Account checkingAccount = AccountFactory.createAccount(
                AccountType.CHECKING,
                null, // let the factory assign ID via constructor if null
                customer,
                new Date(),
                BigDecimal.ZERO
        );

        createAccount(checkingAccount); // persist
        return checkingAccount;
    }

}
