package com.fortisbank.data.repositories;

     import com.fortisbank.models.accounts.Account;
     import com.fortisbank.models.collections.AccountList;
     import com.fortisbank.exceptions.AccountRepositoryException;

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
          * @throws AccountRepositoryException if an error occurs while retrieving the account
          */
         Account getAccountById(String accountId) throws AccountRepositoryException;

         /**
          * Retrieves all accounts associated with a specific customer ID.
          *
          * @param customerId the ID of the customer whose accounts to retrieve
          * @return a list of accounts associated with the specified customer ID
          * @throws AccountRepositoryException if an error occurs while retrieving the accounts
          */
         AccountList getAccountsByCustomerId(String customerId) throws AccountRepositoryException;

         /**
          * Retrieves all accounts.
          *
          * @return a list of all accounts
          * @throws AccountRepositoryException if an error occurs while retrieving the accounts
          */
         AccountList getAllAccounts() throws AccountRepositoryException;

         /**
          * Inserts a new account.
          *
          * @param account the account to insert
          * @throws AccountRepositoryException if an error occurs while inserting the account
          */
         void insertAccount(Account account) throws AccountRepositoryException;

         /**
          * Updates an existing account.
          *
          * @param account the account to update
          * @throws AccountRepositoryException if an error occurs while updating the account
          */
         void updateAccount(Account account) throws AccountRepositoryException;

         /**
          * Deletes an account by its ID.
          *
          * @param accountId the ID of the account to delete
          * @throws AccountRepositoryException if an error occurs while deleting the account
          */
         void deleteAccount(String accountId) throws AccountRepositoryException;
     }