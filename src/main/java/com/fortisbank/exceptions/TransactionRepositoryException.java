package com.fortisbank.exceptions;

/**
 * Custom exception for transaction repository errors.
 */
public class TransactionRepositoryException extends Exception {
    public TransactionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}