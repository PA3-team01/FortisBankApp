package com.fortisbank.models.transactions;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.utils.IdGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public abstract class Transaction implements Serializable, TransactionInterface {
    private static final long serialVersionUID = 1L;

    protected String transactionNumber;
    protected String description;
    protected Date transactionDate;
    protected TransactionType transactionType;
    protected BigDecimal amount;
    protected Account sourceAccount;
    protected Account destinationAccount;

    // Constructor
    public Transaction(String transactionNumber, String description, Date transactionDate,
                       TransactionType transactionType, BigDecimal amount, Account sourceAccount,
                       Account destinationAccount) {
        this.transactionNumber = (transactionNumber != null && !transactionNumber.isEmpty())
                ? transactionNumber
                : IdGenerator.generateId();
        this.description = description;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
    }

    // Abstract method for processing the transaction
    public abstract void processTransaction();

    // Record transaction in account history
    public void recordTransaction() {
        if (sourceAccount != null) sourceAccount.addTransaction(this);
        if (destinationAccount != null) destinationAccount.addTransaction(this);
        System.out.println("Transaction recorded: " + this);
    }

    // Getters
    public String getTransactionNumber() { return transactionNumber; }
    public String getDescription() { return description; }
    public Date getTransactionDate() { return transactionDate; }
    public TransactionType getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public Account getSourceAccount() { return sourceAccount; }
    public Account getDestinationAccount() { return destinationAccount; }

    // Setters
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }
    public void setDescription(String description) { this.description = description; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }
    public void setDestinationAccount(Account destinationAccount) { this.destinationAccount = destinationAccount; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNumber='" + transactionNumber + '\'' +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", sourceAccount=" + (sourceAccount != null ? sourceAccount.getAccountNumber() : "N/A") +
                ", destinationAccount=" + (destinationAccount != null ? destinationAccount.getAccountNumber() : "N/A") +
                '}';
    }

    public BigDecimal getSignedAmountFor(Account contextAccount) {
        switch (transactionType) {
            case DEPOSIT:
                return amount;
            case WITHDRAWAL:
            case FEE:
                return amount.negate();
            case TRANSFER:
                if (sourceAccount != null && sourceAccount.equals(contextAccount)) {
                    return amount.negate();
                } else if (destinationAccount != null && destinationAccount.equals(contextAccount)) {
                    return amount;
                }
                break;
        }
        return BigDecimal.ZERO;
    }




}
