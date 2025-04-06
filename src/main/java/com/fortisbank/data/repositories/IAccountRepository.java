package com.fortisbank.data.repositories;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

/**
 * Interface for account repository operations.
 * Provides methods to manage account data.
 */
public interface IAccountRepository {

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the ID of the account to retrieve
     * @return the account with the specified ID, or null if not found
     */
    Account getAccountById(String accountId);

    /**
     * Retrieves all accounts associated with a specific customer ID.
     *
     * @param customerId the ID of the customer whose accounts to retrieve
     * @return a list of accounts associated with the specified customer ID
     */
    AccountList getAccountsByCustomerId(String customerId);

    /**
     * Retrieves all accounts.
     *
     * @return a list of all accounts
     */
    AccountList getAllAccounts();

    /**
     * Inserts a new account.
     *
     * @param account the account to insert
     */
    void insertAccount(Account account);

    /**
     * Updates an existing account.
     *
     * @param account the account to update
     */
    void updateAccount(Account account);

    /**
     * Deletes an account by its ID.
     *
     * @param accountId the ID of the account to delete
     */
    void deleteAccount(String accountId);
}