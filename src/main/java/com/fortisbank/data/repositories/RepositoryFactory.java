package com.fortisbank.data.repositories;

public class RepositoryFactory {

    private final StorageMode mode;

    public RepositoryFactory(StorageMode mode) {
        this.mode = mode;
    }

    public ICustomerRepository getCustomerRepository() {
        return switch (mode) {
            case FILE -> new CustomerRepositoryFile();
            case DATABASE -> CustomerRepository.getInstance();
        };
    }

    public IAccountRepository getAccountRepository() {
        return switch (mode) {
            case FILE -> new AccountRepositoryFile();
            case DATABASE -> AccountRepository.getInstance();
        };
    }

    public ITransactionRepository getTransactionRepository() {
        return switch (mode) {
            case FILE -> new TransactionRepositoryFile();
            case DATABASE -> TransactionRepository.getInstance();
        };
    }
}

