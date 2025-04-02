package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

public class TransactionRepositoryFile extends FileRepository<Transaction> implements ITransactionRepository {
    private static final File file = new File("data/transactions.ser");
    private static TransactionRepositoryFile instance;

    private TransactionRepositoryFile() {
        super(file);
    }

    public static synchronized TransactionRepositoryFile getInstance() {
        if (instance == null) {
            instance = new TransactionRepositoryFile();
        }
        return instance;
    }

    @Override
    public Transaction getTransactionByNumber(String transactionNumber) {
        return readAll().stream()
                .filter(t -> t.getTransactionNumber().equals(transactionNumber))
                .findFirst()
                .orElse(null);
    }

    // File: src/main/java/com/fortisbank/data/repositories/TransactionRepositoryFile.java

    @Override
    public TransactionList getTransactionsByAccount(String accountId) {
        return readAll().stream()
                .filter(t -> {
                    Account sourceAccount = t.getSourceAccount();
                    return (sourceAccount != null && sourceAccount.getAccountNumber().equals(accountId))
                            || (t.getDestinationAccount() != null
                            && t.getDestinationAccount().getAccountNumber().equals(accountId));
                })
                .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
    }


    @Override
    public TransactionList getAllTransactions() {
        return (TransactionList) readAll();
    }

    @Override
    public void insertTransaction(Transaction transaction) {
        var transactions = readAll();
        transactions.add(transaction);
        writeAll(transactions);
    }

    @Override
    public void deleteTransaction(String transactionNumber) {
        var transactions = readAll();
        transactions.removeIf(t -> t.getTransactionNumber().equals(transactionNumber));
        writeAll(transactions);
    }

    @Override
    public TransactionList getTransactionsByCustomerAndDateRange(String customerID, LocalDate start, LocalDate end) {
        ZoneId zone = ZoneId.systemDefault();
        return readAll().stream()
                .filter(t -> {
                    Account sourceAccount = t.getSourceAccount();
                    if (sourceAccount == null) {
                        return false;
                    }
                    String transactionCustomerID = sourceAccount.getCustomer().getUserId();
                    LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                    return transactionCustomerID.equals(customerID) &&
                            (!transactionDate.isBefore(start) && !transactionDate.isAfter(end));
                })
                .collect(TransactionList::new, TransactionList::add, TransactionList::addAll);
    }

    @Override
    public BigDecimal getBalanceBeforeDate(String customerID, LocalDate start) {
        ZoneId zone = ZoneId.systemDefault();
        return readAll().stream()
                .filter(t -> {
                    String transactionCustomerID = t.getSourceAccount().getCustomer().getUserId();
                    LocalDate transactionDate = t.getTransactionDate().toInstant().atZone(zone).toLocalDate();
                    return transactionCustomerID.equals(customerID) && transactionDate.isBefore(start);
                })
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}