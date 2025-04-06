package com.fortisbank.models.transactions;

/**
 * Enum representing the different types of transactions.
 */
public enum TransactionType {
    /**
     * Represents a deposit transaction.
     */
    DEPOSIT,

    /**
     * Represents a withdrawal transaction.
     */
    WITHDRAWAL,

    /**
     * Represents a transfer transaction.
     */
    TRANSFER,

    /**
     * Represents a fee transaction.
     */
    FEE
}