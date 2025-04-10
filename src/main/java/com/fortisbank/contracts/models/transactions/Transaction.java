package com.fortisbank.contracts.models.transactions;

import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Abstract class representing a generic transaction.
 */
public abstract class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the transaction.
     */
    protected String transactionNumber;

    /**
     * Description of the transaction.
     */
    protected String description;

    /**
     * Date of the transaction.
     */
    protected Date transactionDate;

    /**
     * Type of the transaction.
     */
    protected TransactionType transactionType;

    /**
     * Amount involved in the transaction.
     */
    protected BigDecimal amount;

    /**
     * Source account of the transaction.
     */
    protected Account sourceAccount;

    /**
     * Destination account of the transaction.
     */
    protected Account destinationAccount;

    /**
     * Constructor initializing the transaction with specified values.
     *
     * @param transactionNumber the unique identifier for the transaction
     * @param description the description of the transaction
     * @param transactionDate the date of the transaction
     * @param transactionType the type of the transaction
     * @param amount the amount involved in the transaction
     * @param sourceAccount the source account of the transaction
     * @param destinationAccount the destination account of the transaction
     */
    public Transaction(String transactionNumber, String description, Date transactionDate,
                       TransactionType transactionType, BigDecimal amount,
                       Account sourceAccount, Account destinationAccount) {
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

    /**
     * Returns the unique identifier for the transaction.
     *
     * @return the transaction number
     */
    public String getTransactionNumber() {
        return transactionNumber;
    }

    /**
     * Returns the description of the transaction.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date of the transaction.
     *
     * @return the transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Returns the type of the transaction.
     *
     * @return the transaction type
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Returns the amount involved in the transaction.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the source account of the transaction.
     *
     * @return the source account
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Returns the destination account of the transaction.
     *
     * @return the destination account
     */
    public Account getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * Sets the unique identifier for the transaction.
     *
     * @param transactionNumber the transaction number
     */
    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    /**
     * Sets the description of the transaction.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the date of the transaction.
     *
     * @param transactionDate the transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Sets the type of the transaction.
     *
     * @param transactionType the transaction type
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Sets the amount involved in the transaction.
     *
     * @param amount the amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Sets the source account of the transaction.
     *
     * @param sourceAccount the source account
     */
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    /**
     * Sets the destination account of the transaction.
     *
     * @param destinationAccount the destination account
     */
    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    /**
     * Returns a string representation of the transaction.
     *
     * @return a string representation of the transaction
     */
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

    /**
     * Returns the signed amount for the given context account.
     *
     * @param contextAccount the context account
     * @return the signed amount
     */
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