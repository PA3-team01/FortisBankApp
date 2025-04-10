package com.fortisbank.exceptions;

/**
 * Exception class for handling errors in customer repositories.
 */
public class CustomerRepositoryException extends RuntimeException {
    public CustomerRepositoryException(String message) {
        super(message);
    }

    public CustomerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}