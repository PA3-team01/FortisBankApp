package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.transactions.Transaction;

import java.io.File;
import java.util.List;

public class AccountRepositoryFile extends FileRepository<Account> implements IAccountRepository {
    private static final File file = new File("data/accounts.ser");

    protected AccountRepositoryFile() {
        super(file);
    }

    /**
     * Retrieves an account with the specified account ID from the repository.
     *
     * @param accountId The unique identifier of the account to retrieve.
     * @return The account associated with the specified account ID, or null if no matching account is found.
     */
    @Override
    public Account getAccountById(String accountId) {
        return readAll().stream()
                .filter(a -> a.getAccountNumber().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a list of accounts associated with a specific customer ID.
     *
     * @param customerId the unique identifier of the customer whose accounts are to be retrieved
     * @return an AccountList containing all accounts associated with the provided customer ID
     */
    @Override
    public AccountList getAccountsByCustomerId(String customerId) {
        AccountList accounts = new AccountList();
        for (Account account : readAll()) {
            if (account.getCustomer() != null && customerId.equals(account.getCustomer().getCustomerID())) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    /**
     * Retrieves all account objects stored in the repository.
     *
     * @return an AccountList containing all accounts in the repository
     */
    @Override
    public AccountList getAllAccounts() {
        return (AccountList) readAll();
    }

    /**
     * Inserts a new account into the storage.
     *
     * The method retrieves all existing accounts, adds the specified account
     * to the list, and writes the updated list back to the storage file.
     *
     * @param account the Account object to be inserted into the storage
     */
    @Override
    public void insertAccount(Account account) {
        AccountList accounts = (AccountList) readAll();
        accounts.add(account);
        writeAll(accounts);
    }

    /**
     * Updates an existing account in the persistent storage.
     * The method searches for an account with the same account number as the provided account.
     * If a match is found, the account is updated. The updated list is then written back to the storage.
     *
     * @param account The account object containing updated details. The account to be updated is matched based on its account number.
     */
    @Override
    public void updateAccount(Account account) {
        AccountList accounts = (AccountList) readAll();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                accounts.set(i, account);
                break;
            }
        }
        writeAll(accounts);
    }

    /**
     * Deletes an account from the data storage based on the provided account ID.
     * The method removes the account with the matching account number from the list
     * of accounts and writes the updated list back to the storage.
     *
     * @param accountId The unique identifier of the account to be deleted.
     */
    @Override
    public void deleteAccount(String accountId) {
        AccountList accounts = (AccountList) readAll();
        accounts.removeIf(a -> a.getAccountNumber().equals(accountId));
        writeAll(accounts);
    }

    @Override
    public void recordTransaction(Transaction transaction) {
        //TODO: Implement this method
        // will need to call the TransactionRepositoryFile to record the transaction
        // then update the account

    }

    @Override
    public List<Transaction> getTransactionsForAccount(String accountId) {
        //TODO: Implement this method
        // will need to call the TransactionRepositoryFile to get the transactions
        // filter where the transaction account number matches the given account ID
        return List.of();
    }
}
