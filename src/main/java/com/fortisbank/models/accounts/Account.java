package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.*;
import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class Account implements AccountInterface, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final String accountNumber;
    protected Customer customer;
    protected AccountType accountType;
    protected Date openedDate;
    protected BigDecimal availableBalance;
    protected final TransactionList transactions;
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

    // Deposit
    @Override
    public void deposit(BigDecimal amount) {
        DepositTransaction tx = (DepositTransaction) TransactionFactory.createTransaction(
                TransactionType.DEPOSIT,
                "Deposit to account",
                new Date(),
                amount,
                this,
                null
        );
        tx.processTransaction();
    }

    // Withdraw
    @Override
    public void withdraw(BigDecimal amount) {
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        WithdrawalTransaction tx = (WithdrawalTransaction) TransactionFactory.createTransaction(
                TransactionType.WITHDRAWAL,
                "Withdrawal from account",
                new Date(),
                amount,
                this,
                null
        );
        tx.processTransaction();
    }

    // Transfer
    @Override
    public void transfer(Account targetAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        TransferTransaction tx = (TransferTransaction) TransactionFactory.createTransaction(
                TransactionType.TRANSFER,
                "Transfer to " + targetAccount.getAccountNumber(),
                new Date(),
                amount,
                this,
                targetAccount
        );
        tx.processTransaction();
    }

    // Apply Fee
    public void applyFees(BigDecimal feeAmount, String description) {
        if (availableBalance.compareTo(feeAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance to apply fee.");
        }

        FeeTransaction tx = (FeeTransaction) TransactionFactory.createTransaction(
                TransactionType.FEE,
                description != null ? description : "Fee applied",
                new Date(),
                feeAmount,
                this,
                null
        );
        tx.processTransaction();
    }

    // Add transaction manually (used by transaction.recordTransaction())
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
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

    // Close Account
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
