package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

import java.io.File;

/**
 * Repository class for managing account data stored in a file.
 * Extends the FileRepository class and implements the IAccountRepository interface.
 */
public class AccountRepositoryFile extends FileRepository<Account> implements IAccountRepository {
    private static final File file = new File("data/accounts.ser"); // File to store account data
    private static AccountRepositoryFile instance; // Singleton instance
    private TransactionRepositoryFile transactionRepositoryFile = TransactionRepositoryFile.getInstance(); // Transaction repository instance

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the repository with the specified file.
     */
    private AccountRepositoryFile() {
        super(file);
    }

    /**
     * Returns the singleton instance of AccountRepositoryFile.
     * Synchronized to prevent multiple threads from creating multiple instances.
     *
     * @return the singleton instance of AccountRepositoryFile
     */
    public static synchronized AccountRepositoryFile getInstance() {
        if (instance == null) {
            instance = new AccountRepositoryFile();
        }
        return instance;
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the ID of the account to retrieve
     * @return the account with the specified ID, or null if not found
     */
    @Override
    public Account getAccountById(String accountId) {
        return readAll().stream()
                .filter(a -> a.getAccountNumber().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all accounts associated with a specific customer ID.
     *
     * @param customerId the ID of the customer whose accounts to retrieve
     * @return a list of accounts associated with the specified customer ID
     */
    @Override
    public AccountList getAccountsByCustomerId(String customerId) {
        AccountList accounts = new AccountList();
        for (Account account : readAll()) {
            if (account.getCustomer() != null && customerId.equals(account.getCustomer().getUserId())) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    /**
     * Retrieves all accounts from the file.
     *
     * @return a list of all accounts
     */
    @Override
    public AccountList getAllAccounts() {
        return new AccountList(readAll());
    }

    /**
     * Inserts a new account into the file.
     *
     * @param account the account to insert
     */
    @Override
    public void insertAccount(Account account) {
        AccountList accounts = new AccountList(readAll());
        accounts.add(account);
        writeAll(accounts);
    }

    /**
     * Updates an existing account in the file.
     *
     * @param account the account to update
     */
    @Override
    public void updateAccount(Account account) {
        AccountList accounts = new AccountList(readAll());
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                accounts.set(i, account);
                break;
            }
        }
        writeAll(accounts);
    }

    /**
     * Deletes an account from the file.
     *
     * @param accountId the ID of the account to delete
     */
    @Override
    public void deleteAccount(String accountId) {
        AccountList accounts = new AccountList(readAll());
        accounts.removeIf(a -> a.getAccountNumber().equals(accountId));
        writeAll(accounts);
    }
}