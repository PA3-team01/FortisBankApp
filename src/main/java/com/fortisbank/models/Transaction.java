package com.fortisbank.models;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    // ATTRIBUTES
    private String transactionNumber;
    private String description;
    private Date transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private Account sourceAccount;
    private Account destinationAccount;
    private BigDecimal fees;


// GET + SET

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    // TO DO: VERIFICATION OF BigDecimal fees

    //SAVE THE TRANSACTION

    public void recordTransaction() {
        if (sourceAccount != null) {
            sourceAccount.addTransaction(this); //ADD THE TRANSAC TO THE SOURCE ACCOUNT
        }
        if (destinationAccount != null) {
            destinationAccount.addTransaction(this); //ADD THE TRANSAC TO THE MAIN ACCOUNT
        }
        System.out.println("Transaction recorded : " + this);
    }

    //PROCESS THE TRANSACTION ACCORDING TO HIS OWN TYPE

    public void processTransaction() {
        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                if (destinationAccount != null) {
                    destinationAccount.deposit(amount); //DEPOSIT IN THE DESTINATION ACCOUNT
                }
                else {
                    throw new IllegalArgumentException("Destination account missing for a deposit.");
                }
                break;
            case "WITHDRAW":
                if (sourceAccount != null) {
                    sourceAccount.withdraw(amount, fees); //WIDRAW FROM ACCOUNT SOURCE
                }
                else {
                    throw new IllegalArgumentException("Account missing for withdrawal");
                }
                break;
            case "TRANSFER":
                if (sourceAccount != null && destinationAccount != null) {
                    sourceAccount.transfer(destinationAccount, amount); //TRANSFER BETTWEEN ACC.
                }
                else {
                    throw new IllegalArgumentException("Destination account is missing for a transfer");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type : " + transactionType);
        }
        System.out.println("Transaction processed : " + this);
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