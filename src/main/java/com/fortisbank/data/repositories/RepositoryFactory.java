package com.fortisbank.data.repositories;

public class RepositoryFactory {

    private static RepositoryFactory instance;
    private final StorageMode mode;

    private RepositoryFactory(StorageMode mode) {
        this.mode = mode;
    }

    public static synchronized RepositoryFactory getInstance(StorageMode mode) {
        if (instance == null) {
            instance = new RepositoryFactory(mode);
        }
        return instance;
    }

    public ICustomerRepository getCustomerRepository() {
        return switch (mode) {
            case FILE -> CustomerRepositoryFile.getInstance();
            case DATABASE -> CustomerRepository.getInstance();
        };
    }

    public IAccountRepository getAccountRepository() {
        return switch (mode) {
            case FILE -> AccountRepositoryFile.getInstance();
            case DATABASE -> AccountRepository.getInstance();
        };
    }

    public ITransactionRepository getTransactionRepository() {
        return switch (mode) {
            case FILE -> TransactionRepositoryFile.getInstance();
            case DATABASE -> TransactionRepository.getInstance();
        };
    }
}