package com.fortisbank.models;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String TransactionNumber;
    private String Description;
    private Date TransactionDate;
    private String TransactionType;
    private double Amount;
    private Account SourceAccount;
    private Account DestinationAccount;

    public Transaction(String transactionNumber, String description, String transactionType, double amount,
                       Account sourceAccount, Account destinationAccount) {
        this.TransactionNumber = transactionNumber;
        this.Description = description;
        this.TransactionDate = new Date();
        this.TransactionType = transactionType;
        this.Amount = amount;
        this.SourceAccount = sourceAccount;
        this.DestinationAccount = destinationAccount;
    }

    //get/set
    public String getTransactionNumber() {
        return TransactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.TransactionNumber = transactionNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public Date getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.TransactionDate = transactionDate;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        this.TransactionType = transactionType;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        this.Amount = amount;
    }

    public Account getSourceAccount() {
        return SourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.SourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return DestinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.DestinationAccount = destinationAccount;
    }

    //public void RecordTransaction(){
   // }

    //public void ProcessTransaction(){
    //}


    @Override
    public String toString() {
        return "Transaction{" +
                "TransactionNumber='" + TransactionNumber + '\'' +
                ", Description= " + Description + '\'' +
                ", TransactionDate=" + TransactionDate +
                ", TransactionType='" + TransactionType + '\'' +
                ", Amount=" + Amount +
                ", SourceAccount=" + SourceAccount +
                ", DestinationAccount=" + DestinationAccount +
                '}';
    }
}
