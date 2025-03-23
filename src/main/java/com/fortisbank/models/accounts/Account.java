package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.IdGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Account implements AccountInterface, Serializable {
    private static final long serialVersionUID = 1L;

    protected String accountNumber;
    protected Customer customer;
    protected AccountType accountType;
    protected Date openedDate;
    protected BigDecimal availableBalance;
    protected TransactionList transactions;
    protected boolean isActive;

    public Account() {
        this.accountNumber = IdGenerator.generateId();
        this.transactions = new TransactionList();
        this.availableBalance = BigDecimal.ZERO;
        this.isActive = true;
    }

    public Account(String accountNumber, Customer customer, AccountType accountType, Date openedDate, BigDecimal initialBalance) {
        this.accountNumber = (accountNumber != null) ? accountNumber : IdGenerator.generateId();
        this.customer = customer;
        this.accountType = accountType;
        this.openedDate = openedDate;
        this.availableBalance = initialBalance;
        this.transactions = new TransactionList();
        this.isActive = true;
    }

    @Override
    public void deposit(BigDecimal amount) {
        availableBalance = availableBalance.add(amount);
        recordTransaction(TransactionType.DEPOSIT, amount, this, null);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        availableBalance = availableBalance.subtract(amount);
        recordTransaction(TransactionType.WITHDRAWAL, amount, this, null);
    }

    @Override
    public void transfer(Account targetAccount, BigDecimal amount) {
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        this.withdraw(amount);
        targetAccount.deposit(amount);
        recordTransaction(TransactionType.TRANSFER, amount, this, targetAccount);
    }

    private void recordTransaction(TransactionType type, BigDecimal amount, Account source, Account destination) {
        Transaction transaction = (Transaction) TransactionFactory.createTransaction(
                type,
                "Transaction: " + type,
                new Date(),
                amount,
                source,
                destination
        );
        transactions.add(transaction);
    }

    public void applyFees(BigDecimal fees, String description) {
        if (availableBalance.compareTo(fees) >= 0) {
            availableBalance = availableBalance.subtract(fees);
            recordTransaction(TransactionType.FEE, fees, this, null);
        } else {
            throw new IllegalArgumentException("Insufficient balance to apply fees.");
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public AccountType getAccountType() { return accountType; }
    public Date getOpenedDate() { return openedDate; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public List<Transaction> getTransactions() { return transactions; }
    public boolean isActive() { return isActive; }

    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public void setActive(boolean active) { this.isActive = active; }

    public void closeAccount() {
        if (availableBalance.compareTo(BigDecimal.ZERO) == 0) {
            isActive = false;
            System.out.println("Account " + accountNumber + " has been closed.");
        } else {
            throw new IllegalStateException("Unable to close the account: balance is not zero.");
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", customer=" + (customer != null ? customer.getFullName() : "N/A") +
                ", accountType=" + accountType +
                ", openedDate=" + openedDate +
                ", availableBalance=" + availableBalance +
                ", isActive=" + isActive +
                '}';
    }
}
