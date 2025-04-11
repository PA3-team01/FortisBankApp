package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.collections.TransactionList;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Abstract class representing a bank account.
 * Implements Serializable interface for object serialization.
 */
public abstract class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique account number.
     */
    protected String accountNumber;

    /**
     * Customer who owns the account.
     */
    protected Customer customer;

    /**
     * Type of the account.
     */
    protected AccountType accountType;

    /**
     * Date when the account was opened.
     */
    protected Date openedDate;

    /**
     * Available balance in the account.
     */
    protected BigDecimal availableBalance;

    /**
     * List of transactions associated with the account.
     * Marked as transient to prevent serialization.
     */
    protected transient TransactionList transactions;

    /**
     * Indicates whether the account is active.
     */
    protected boolean isActive;

    /**
     * Indicates whether a low balance alert has been sent.
     */
    private boolean lowBalanceAlertSent = false;

    /**
     * Default constructor initializing default values.
     */
    public Account() {
        this.accountNumber = IdGenerator.generateId();
        this.transactions = new TransactionList();
        this.availableBalance = BigDecimal.ZERO;
        this.isActive = true;
    }

    /**
     * Parameterized constructor initializing account with specified values.
     *
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param accountType the type of the account
     * @param openedDate the date when the account was opened
     * @param initialBalance the initial balance of the account
     */
    public Account(String accountNumber, Customer customer, AccountType accountType, Date openedDate, BigDecimal initialBalance) {
        this.accountNumber = (accountNumber != null) ? accountNumber : IdGenerator.generateId();
        this.customer = customer;
        this.accountType = accountType;
        this.openedDate = openedDate;
        this.availableBalance = initialBalance;
        this.transactions = new TransactionList();
        this.isActive = true;
    }

    /**
     * Adds a transaction to the in-memory transaction history.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        if (transactions != null) {
            transactions.add(transaction);
        }
    }

    // Getters

    /**
     * Returns the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns the customer who owns the account.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the type of the account.
     *
     * @return the account type
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Returns the date when the account was opened.
     *
     * @return the opened date
     */
    public Date getOpenedDate() {
        return openedDate;
    }

    /**
     * Returns the available balance in the account.
     *
     * @return the available balance
     */
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    /**
     * Returns the list of transactions associated with the account.
     *
     * @return the transaction list
     */
    public TransactionList getTransactions() {
        return transactions;
    }

    /**
     * Returns whether the account is active.
     *
     * @return true if the account is active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    // Setters

    /**
     * Sets the account number.
     *
     * @param accountNumber the account number to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Sets the available balance in the account.
     *
     * @param availableBalance the available balance to set
     */
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    /**
     * Sets the active status of the account.
     *
     * @param active the active status to set
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Utility

    /**
     * Checks if the account has sufficient funds for a given amount.
     *
     * @param amount the amount to check
     * @return true if the account has sufficient funds, false otherwise
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return availableBalance.compareTo(amount) >= 0;
    }

    /**
     * Returns whether a low balance alert has been sent.
     *
     * @return true if a low balance alert has been sent, false otherwise
     */
    public boolean isLowBalanceAlertSent() {
        return lowBalanceAlertSent;
    }

    /**
     * Sets the low balance alert sent status.
     *
     * @param lowBalanceAlertSent the status to set
     */
    public void setLowBalanceAlertSent(boolean lowBalanceAlertSent) {
        this.lowBalanceAlertSent = lowBalanceAlertSent;
    }

    /**
     * Returns a string representation of the account.
     *
     * @return a string representation of the account
     */
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

    /**
     * Displays account information.
     *
     * @return a string containing account information
     */
    public abstract String displayAccountInfo();

    /**
     * Returns the credit limit of the account.
     *
     * @return the credit limit
     */
    public abstract BigDecimal getCreditLimit();

    /**
     * Checks if this account is equal to another object.
     *
     * @param obj the object to compare
     * @return true if this account is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account other = (Account) obj;
        return accountNumber != null && accountNumber.equals(other.accountNumber);
    }

    /**
     * Returns the hash code of the account.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }
}