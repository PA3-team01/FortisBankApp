package com.fortisbank.business.services.account;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

/**
* Interface for account-related operations.
*/
public interface IAccountService {

/**
 * Creates a new account.
 *
 * @param account the account to be created
 */
void createAccount(Account account);

/**
 * Updates the given account.
 *
 * @param account the account to be updated
 */
void updateAccount(Account account);

/**
 * Deletes the account with the given ID.
 *
 * @param accountId the ID of the account to be deleted
 */
void deleteAccount(String accountId);

/**
 * Retrieves the account with the given ID.
 *
 * @param accountId the ID of the account to be retrieved
 * @return the account with the given ID
 */
Account getAccount(String accountId);

/**
 * Retrieves the accounts associated with the given customer ID.
 *
 * @param customerId the ID of the customer whose accounts are to be retrieved
 * @return the list of accounts associated with the given customer ID
 */
AccountList getAccountsByCustomerId(String customerId);

/**
 * Retrieves all accounts.
 *
 * @return the list of all accounts
 */
AccountList getAllAccounts();
}