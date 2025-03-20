package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.utils.IdGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Account {
    private String accountNumber;
    private Customer customer;
    private AccountType accountType;
    private Date openedDate;
    private BigDecimal availableBalance;
    private List<Transaction> transactions;
    private boolean isActive;

    // Default Constructor
    public Account() {
        this.accountNumber = IdGenerator.generateId();
        this.transactions = new ArrayList<>();
        this.availableBalance = BigDecimal.ZERO;
        this.isActive = true;
    }

    // Constructor with Initial Values
    public Account(String accountNumber, Customer customer, AccountType accountType, Date openedDate, BigDecimal initialBalance) {
        this.accountNumber = (accountNumber != null) ? accountNumber : IdGenerator.generateId();
        this.customer = customer;
        this.accountType = accountType;
        this.openedDate = openedDate;
        this.availableBalance = initialBalance;
        this.transactions = new ArrayList<>();
        this.isActive = true;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public AccountType getAccountType() { return accountType; }
    public Date getOpenedDate() { return openedDate; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public List<Transaction> getTransactions() { return transactions; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setOpenedDate(Date openedDate) { this.openedDate = openedDate; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public void setActive(boolean active) { isActive = active; }

    // Balance Inquiry
    public BigDecimal viewBalance() {
        return availableBalance;
    }

    // Adds a transaction to the account
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    // Apply Fees (Refactored to Use `FeeTransaction`)
    public void applyFees(BigDecimal fees, String description) {
        if (availableBalance.compareTo(fees) >= 0) {
            availableBalance = availableBalance.subtract(fees);

            // Create and add a FeeTransaction using TransactionFactory
            Transaction feeTransaction = (Transaction) TransactionFactory.createTransaction(
                    TransactionType.FEE,
                    description,
                    new Date(),
                    fees,
                    this,
                    null
            );
            addTransaction(feeTransaction);
        } else {
            throw new IllegalArgumentException("Insufficient balance to apply fees.");
        }
    }

    // Close Account
    public void closeAccount() {
        if (availableBalance.compareTo(BigDecimal.ZERO) == 0) {
            isActive = false;
            System.out.println("Account " + accountNumber + " has been closed.");
        } else {
            throw new IllegalStateException("Unable to close the account: balance is not zero.");
        }
    }

    // Abstract Methods for Account-Specific Operations
    public abstract void deposit(BigDecimal amount);
    public abstract void withdraw(BigDecimal amount);
    public abstract void transfer(Account targetAccount, BigDecimal amount);

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
