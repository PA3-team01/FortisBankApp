package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final String accountNumber;
    protected Customer customer;
    protected AccountType accountType;
    protected Date openedDate;
    protected BigDecimal availableBalance;

    // Prevents transactions from being serialized to file — for runtime only.
    protected transient TransactionList transactions;

    protected boolean isActive;

    // Constructors
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

    // Add transaction to the in-memory transaction history (for business use only)
    public void addTransaction(Transaction transaction) {
        if (transactions != null) {
            transactions.add(transaction);
        }
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Date getOpenedDate() {
        return openedDate;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Utility
    public boolean hasSufficientFunds(BigDecimal amount) {
        return availableBalance.compareTo(amount) >= 0;
    }

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
                ", transactionList=" + transactions +
                ", isActive=" + isActive +
                '}';
    }

    public abstract BigDecimal getCreditLimit();
}
