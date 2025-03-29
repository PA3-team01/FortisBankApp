package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;

import java.io.File;

public class AccountRepositoryFile extends FileRepository<Account> implements IAccountRepository {
    private static final File file = new File("data/accounts.ser");
    private static AccountRepositoryFile instance;
    private TransactionRepositoryFile transactionRepositoryFile = TransactionRepositoryFile.getInstance();

    private AccountRepositoryFile() {
        super(file);
    }

    public static synchronized AccountRepositoryFile getInstance() { // synchronized method to prevent multiple threads from creating multiple instances
        if (instance == null) {
            instance = new AccountRepositoryFile();
        }
        return instance;
    }

    @Override
    public Account getAccountById(String accountId) {
        return readAll().stream()
                .filter(a -> a.getAccountNumber().equals(accountId))
                .findFirst()
                .orElse(null);
    }

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

    @Override
    public AccountList getAllAccounts() {
        return (AccountList) readAll();
    }

    @Override
    public void insertAccount(Account account) {
        AccountList accounts = (AccountList) readAll();
        accounts.add(account);
        writeAll(accounts);
    }

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

    @Override
    public void deleteAccount(String accountId) {
        AccountList accounts = (AccountList) readAll();
        accounts.removeIf(a -> a.getAccountNumber().equals(accountId));
        writeAll(accounts);
    }

}