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

    // Future extension
    // public IAccountRepository getAccountRepository() { ... }
    // public ITransactionRepository getTransactionRepository() { ... }
}
