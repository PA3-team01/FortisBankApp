package com.fortisbank.models;

import com.fortisbank.utils.IdGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Account {
    private String accountNumber;
    private Customer customer; // Full Customer object for easy access to customer details (serializable)
    private String accountType;
    private Date openedDate;
    private BigDecimal availableBalance;
    private List<Transaction> transactions;
    private BigDecimal transactionFees;
    private boolean isActive;

    public Account() {
        this.accountNumber = IdGenerator.generateId();
        this.transactions = new ArrayList<>();
        this.transactionFees = BigDecimal.ZERO;
        this.isActive = true;
    }

    public Account(String accountNumber, Customer customer, String accountType, Date openedDate, BigDecimal initialBalance) {
        this.accountNumber = (accountNumber != null) ? accountNumber : IdGenerator.generateId();
        this.customer = customer;
        this.accountType = accountType;
        this.openedDate = openedDate;
        this.availableBalance = initialBalance;
        this.transactions = new ArrayList<>();
        this.transactionFees = BigDecimal.ZERO;
        this.isActive = true;
    }

    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public String getAccountType() { return accountType; }
    public Date getOpenedDate() { return openedDate; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public List<Transaction> getTransactions() { return transactions; }
    public BigDecimal getTransactionFees() { return transactionFees; }
    public boolean isActive() { return isActive; }

    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setOpenedDate(Date openedDate) { this.openedDate = openedDate; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public void setTransactionFees(BigDecimal transactionFees) { this.transactionFees = transactionFees; }
    public void setActive(boolean active) { isActive = active; }

    public BigDecimal viewBalance() {
        return availableBalance;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    // TODO: Check proposition for fees in Account class and modify accordingly if needed
    public void applyFees(BigDecimal fees, String description) {
        if (availableBalance.compareTo(fees) >= 0) {
            availableBalance = availableBalance.subtract(fees);
            transactionFees = transactionFees.add(fees);
            Transaction feeTransaction = new Transaction(
                    description,
                    new Date(),
                    "FEE",
                    fees,
                    this
            );
            addTransaction(feeTransaction);
        } else {
            throw new IllegalArgumentException("Insufficient balance to apply fees.");
        }
    }

    public void closeAccount() {
        if (availableBalance.compareTo(BigDecimal.ZERO) == 0) {
            isActive = false;
            System.out.println("Account " + accountNumber + " has been closed.");
        } else {
            throw new IllegalStateException("Unable to close the account: balance is not zero.");
        }
    }

    public abstract void deposit(BigDecimal amount);
    public abstract void withdraw(BigDecimal amount, BigDecimal fees);
    public abstract void transfer(Account targetAccount, BigDecimal amount);
}
