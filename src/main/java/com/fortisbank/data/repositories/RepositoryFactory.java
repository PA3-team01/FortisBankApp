package com.fortisbank.data.repositories;

public class RepositoryFactory {

    private final StorageMode mode;

    public RepositoryFactory(StorageMode mode) {
        this.mode = mode;
    }

    public ICustomerRepository getCustomerRepository() {
        return switch (mode) {
            case FILE -> new CustomerRepositoryFile();
            case DATABASE -> new CustomerRepository();
        };
    }

    public IAccountRepository getAccountRepository() {
        return switch (mode) {
            case FILE -> new AccountRepositoryFile();
            case DATABASE -> new AccountRepository();
        };
    }

    public ITransactionRepository getTransactionRepository() {
        return switch (mode) {
            case FILE -> new TransactionRepositoryFile();
            case DATABASE -> new TransactionRepository();
        };
    }
}

