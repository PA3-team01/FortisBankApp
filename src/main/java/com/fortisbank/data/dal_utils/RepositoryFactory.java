package com.fortisbank.data.dal_utils;

import com.fortisbank.data.database.*;
import com.fortisbank.data.file.*;
import com.fortisbank.data.interfaces.*;

import java.util.EnumMap;
import java.util.Map;

public class RepositoryFactory {

    private static final Map<StorageMode, RepositoryFactory> instances = new EnumMap<>(StorageMode.class);

    private final StorageMode mode;

    private RepositoryFactory(StorageMode mode) {
        this.mode = mode;
    }

    public static synchronized RepositoryFactory getInstance(StorageMode mode) {
        return instances.computeIfAbsent(mode, RepositoryFactory::new);
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

    public IBankManagerRepository getBankManagerRepository() {
        return switch (mode) {
            case FILE -> BankManagerRepositoryFile.getInstance();
            case DATABASE -> BankManagerRepository.getInstance();
        };
    }

    public INotificationRepository getNotificationRepository() {
        return switch (mode) {
            case FILE -> NotificationRepositoryFile.getInstance();
            case DATABASE -> NotificationRepository.getInstance();
        };
    }
}
