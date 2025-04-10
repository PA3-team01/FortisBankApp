package com.fortisbank.contracts.models.others;

/**
 * Enum representing different types of notifications.
 */
public enum NotificationType {
    /**
     * For manager inbox: approve/reject new account requests.
     */
    ACCOUNT_OPENING_REQUEST,

    /**
     * Confirmation of a completed transaction.
     */
    TRANSACTION_RECEIPT,

    /**
     * Notification sent to customer after approval.
     */
    ACCOUNT_APPROVAL,

    /**
     * Notification sent to customer after rejection.
     */
    ACCOUNT_REJECTION,

    /**
     * Received internal message (manager ↔ customer).
     */
    NEW_MESSAGE,

    /**
     * Suspicious login, password reset, etc.
     */
    SECURITY_ALERT,

    /**
     * App update or maintenance announcement.
     */
    SYSTEM_UPDATE,

    /**
     * Free-form event.
     */
    CUSTOM,

    /**
     * Generic information.
     */
    INFO,

    /**
     * Non-critical alert.
     */
    WARNING,

    /**
     * Critical issue or operation failure.
     */
    ERROR
}