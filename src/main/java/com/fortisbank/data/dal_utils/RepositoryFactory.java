package com.fortisbank.data.dal_utils;

import com.fortisbank.data.database.AccountRepository;
import com.fortisbank.data.database.BankManagerRepository;
import com.fortisbank.data.database.CustomerRepository;
import com.fortisbank.data.database.TransactionRepository;
import com.fortisbank.data.file.AccountRepositoryFile;
import com.fortisbank.data.file.BankManagerRepositoryFile;
import com.fortisbank.data.file.CustomerRepositoryFile;
import com.fortisbank.data.file.TransactionRepositoryFile;
import com.fortisbank.data.interfaces.IAccountRepository;
import com.fortisbank.data.interfaces.IBankManagerRepository;
import com.fortisbank.data.interfaces.ICustomerRepository;
import com.fortisbank.data.interfaces.ITransactionRepository;

import java.util.EnumMap;
import java.util.Map;

/**
 * The RepositoryFactory class is designed to provide a single instance of repository objects
 * based on the specified storage mode (FILE or DATABASE). It uses the Singleton pattern to
 * ensure that only one instance of RepositoryFactory exists per StorageMode.
 *
 * - The `instances` map holds the RepositoryFactory instances, keyed by StorageMode.
 * - The constructor is private to prevent direct instantiation.
 * - The `getInstance` method returns the existing instance for the given StorageMode or creates a new one if it doesn't exist.
 * - The `getCustomerRepository`, `getAccountRepository`, and `getTransactionRepository` methods return the appropriate repository
 *   instance based on the current storage mode.
 */
public class RepositoryFactory {

    /**
     * Map to hold the RepositoryFactory instances, keyed by StorageMode.
     */
    private static final Map<StorageMode, RepositoryFactory> instances = new EnumMap<>(StorageMode.class);

    /**
     * The storage mode for this instance of RepositoryFactory.
     */
    private final StorageMode mode;

    /**
     * Private constructor to prevent direct instantiation.
     *
     * @param mode the storage mode for this instance
     */
    private RepositoryFactory(StorageMode mode) {
        this.mode = mode;
    }

    /**
     * Returns the existing instance for the given StorageMode or creates a new one if it doesn't exist.
     * Synchronized to ensure thread safety.
     *
     * @param mode the storage mode for which to get the RepositoryFactory instance
     * @return the RepositoryFactory instance for the specified storage mode
     */
    public static synchronized RepositoryFactory getInstance(StorageMode mode) {
        return instances.computeIfAbsent(mode, RepositoryFactory::new);
    }

    /**
     * Returns the appropriate customer repository instance based on the current storage mode.
     *
     * @return the customer repository instance
     */
    public ICustomerRepository getCustomerRepository() {
        return switch (mode) {
            case FILE -> CustomerRepositoryFile.getInstance();
            case DATABASE -> CustomerRepository.getInstance();
        };
    }

    /**
     * Returns the appropriate account repository instance based on the current storage mode.
     *
     * @return the account repository instance
     */
    public IAccountRepository getAccountRepository() {
        return switch (mode) {
            case FILE -> AccountRepositoryFile.getInstance();
            case DATABASE -> AccountRepository.getInstance();
        };
    }

    /**
     * Returns the appropriate transaction repository instance based on the current storage mode.
     *
     * @return the transaction repository instance
     */
    public ITransactionRepository getTransactionRepository() {
        return switch (mode) {
            case FILE -> TransactionRepositoryFile.getInstance();
            case DATABASE -> TransactionRepository.getInstance();
        };
    }

    /**
     * Returns the appropriate bank manager repository instance based on the current storage mode.
     *
     * @return the bank manager repository instance
     */
    public IBankManagerRepository getBankManagerRepository() {
        return switch (mode) {
            case FILE -> BankManagerRepositoryFile.getInstance();
            case DATABASE -> BankManagerRepository.getInstance();
        };
    }
}