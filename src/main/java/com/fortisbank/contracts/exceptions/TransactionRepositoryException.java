package com.fortisbank.contracts.exceptions;

/**
 * Exception class for handling errors related to transaction repository operations.
 */
public class TransactionRepositoryException extends Exception {

    /**
     * Constructs a new TransactionRepositoryException with no detail message or cause.
     */
    public TransactionRepositoryException() {
        super();
    }

    /**
     * Constructs a new TransactionRepositoryException with the specified detail message.
     *
     * @param message the detail message
     */
    public TransactionRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new TransactionRepositoryException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public TransactionRepositoryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new TransactionRepositoryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public TransactionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new TransactionRepositoryException with formatted message and original cause.
     *
     * @param template the message template
     * @param cause the cause of the exception
     * @param args arguments to format the template
     */
    public TransactionRepositoryException(String template, Throwable cause, Object... args) {
        super(String.format(template, args), cause);
    }
}
