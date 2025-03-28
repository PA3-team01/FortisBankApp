package com.fortisbank.business.services;

import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import java.util.Map;
import java.util.EnumMap;

public class AccountService implements IAccountService {
    private static final Map<StorageMode, AccountService> instances = new EnumMap<>(StorageMode.class);
    private final IAccountRepository accountRepository;

    /**
     * The AccountService class is designed to manage account-related operations
     * using different storage modes (FILE or DATABASE). It follows the Singleton
     * pattern to ensure that only one instance of AccountService exists per StorageMode.
     *
     * - The `instances` map holds AccountService instances, keyed by StorageMode.
     * - The constructor is private to prevent direct instantiation and initializes
     *   the repository factory and account repository based on the given storage mode.
     * - The `getInstance` method returns the existing instance for the given StorageMode
     *   or creates a new one if it doesn't exist.
     * - The CRUD methods (createAccount, updateAccount, deleteAccount, getAccount,
     *   getAccountsByCustomerId, getAllAccounts) delegate operations to the appropriate
     *   repository based on the current storage mode.
     * - The `recordTransaction` and `getTransactionsForAccount` methods handle account
     *   transactions and retrieve transactions for a specific account.
     *
     * This design allows the application to switch between different storage modes
     * seamlessly while ensuring that each storage mode has its own dedicated service
     * instance.
     */
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

    @Override
    public void recordTransaction(Transaction transaction) {
        accountRepository.recordTransaction(transaction);
    }

    @Override
    public TransactionList getTransactionsForAccount(String accountId) {
        return accountRepository.getTransactionsForAccount(accountId);
    }
}