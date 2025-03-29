package com.fortisbank.exceptions;

/**
 * Thrown when a bank manager is not found in the system.
 */
public class ManagerNotFoundException extends RuntimeException {

    public ManagerNotFoundException(String message) {
        super(message);
    }

    public ManagerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
