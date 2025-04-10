package com.fortisbank.exceptions;

/**
 * Exception class for handling errors in bank manager repositories.
 */
public class BankManagerRepositoryException extends RuntimeException {
    public BankManagerRepositoryException(String message) {
        super(message);
    }

    public BankManagerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}