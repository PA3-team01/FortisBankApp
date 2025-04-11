package com.fortisbank.data.dal_utils;

/**
 * Custom exception for notification repository operations.
 */
public class NotificationRepositoryException extends Exception {

    public NotificationRepositoryException(String message) {
        super(message);
    }

    public NotificationRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}


