package com.fortisbank.exceptions;

/**
 * Custom exception for database connection errors.
 */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}