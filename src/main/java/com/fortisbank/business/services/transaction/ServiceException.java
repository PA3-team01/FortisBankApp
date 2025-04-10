package com.fortisbank.business.services.transaction;

/**
 * Generic exception class for service-level errors.
 */
public class ServiceException extends RuntimeException {

    /**
     * Constructs a new ServiceException with the specified detail message.
     *
     * @param message the detail message
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ServiceException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }
}