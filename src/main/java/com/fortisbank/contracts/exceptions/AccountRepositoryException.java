package com.fortisbank.contracts.exceptions;

/**
 * Custom exception for account repository errors.
 */
public class AccountRepositoryException extends Exception {

    /**
     * Constructs a new AccountRepositoryException with the specified detail message.
     *
     * @param message the detail message
     */
    public AccountRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountRepositoryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public AccountRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}