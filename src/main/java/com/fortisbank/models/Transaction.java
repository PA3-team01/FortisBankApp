package com.fortisbank.models;

import com.fortisbank.utils.IdGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;



    private String transactionNumber;
    private String description;
    private Date transactionDate;
    private String transactionType; // TODO: Refactor to use an Enum (TransactionType)
    private BigDecimal amount;
    private Account sourceAccount;
    private Account destinationAccount;
    // TODO: Question--> FEES are simply another transaction type right? maybe we can simplify this by removing fees and just adding a transaction type for fees
    private BigDecimal fees;

    // Default Constructor for Serialization and Database Mapping
    public Transaction() {}

    // Main Constructor for Transactions
    public Transaction(String transactionNumber, String description, Date transactionDate,
                       String transactionType, BigDecimal amount, Account sourceAccount,
                       Account destinationAccount, BigDecimal fees) {
        this.transactionNumber = (transactionNumber != null && !transactionNumber.isEmpty())
                ? transactionNumber
                : IdGenerator.generateId(); // Generate if missing
        this.description = description;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.fees = (fees != null) ? fees : BigDecimal.ZERO;
    }

    // Simplified Constructor Without Fees (Deposit/Withdraw Cases)
    public Transaction(String description, Date transactionDate, String transactionType,
                       BigDecimal amount, Account sourceAccount) {
        this(null, description, transactionDate, transactionType, amount, sourceAccount, null, BigDecimal.ZERO);
    }

    // Simplified Constructor for Fees
    public Transaction(String description, Date transactionDate, BigDecimal fees, Account sourceAccount) {
        this(null, description, transactionDate, "FEE", fees, sourceAccount, null, fees);
    }



    // Getters
    public String getTransactionNumber() { return transactionNumber; }
    public String getDescription() { return description; }
    public Date getTransactionDate() { return transactionDate; }
    public String getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public Account getSourceAccount() { return sourceAccount; }
    public Account getDestinationAccount() { return destinationAccount; }
    public BigDecimal getFees() { return fees; }

    // Setters
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }
    public void setDescription(String description) { this.description = description; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }
    public void setDestinationAccount(Account destinationAccount) { this.destinationAccount = destinationAccount; }
    public void setFees(BigDecimal fees) { this.fees = fees; }

    // Saves the Transaction in the Account History
    public void recordTransaction() {
        if (sourceAccount != null) {
            sourceAccount.addTransaction(this);
        }
        if (destinationAccount != null) {
            destinationAccount.addTransaction(this);
        }
        System.out.println("Transaction recorded: " + this);
    }

    // Processes the Transaction Based on Type
    public void processTransaction() {
        if (transactionType == null || transactionType.isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty.");
        }

        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                if (destinationAccount != null) {
                    destinationAccount.deposit(amount);
                } else {
                    throw new IllegalArgumentException("Destination account missing for deposit.");
                }
                break;
            case "WITHDRAW":
                if (sourceAccount != null) {
                    sourceAccount.withdraw(amount, fees);
                } else {
                    throw new IllegalArgumentException("Source account missing for withdrawal.");
                }
                break;
            case "TRANSFER":
                if (sourceAccount != null && destinationAccount != null) {
                    sourceAccount.transfer(destinationAccount, amount);
                } else {
                    throw new IllegalArgumentException("Source or destination account missing for transfer.");
                }
                break;
            case "FEE":
                if (sourceAccount != null) {
                    sourceAccount.applyFees(fees, description);
                } else {
                    throw new IllegalArgumentException("Source account missing for fee application.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + transactionType);
        }
        System.out.println("Transaction processed: " + this);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNumber='" + transactionNumber + '\'' +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", fees=" + fees +
                ", sourceAccount=" + (sourceAccount != null ? sourceAccount.getAccountNumber() : "N/A") +
                ", destinationAccount=" + (destinationAccount != null ? destinationAccount.getAccountNumber() : "N/A") +
                '}';
    }
}
