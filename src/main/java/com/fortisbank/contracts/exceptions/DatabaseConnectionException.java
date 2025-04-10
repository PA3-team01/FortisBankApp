package com.fortisbank.contracts.exceptions;

/**
 * Custom exception for database connection errors.
 */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}