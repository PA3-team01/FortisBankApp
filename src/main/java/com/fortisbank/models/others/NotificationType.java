package com.fortisbank.models.others;

public enum NotificationType {
    ACCOUNT_OPENING_REQUEST, // For manager inbox: approve/reject new account requests
    TRANSACTION_RECEIPT,     // Confirmation of a completed transaction
    ACCOUNT_APPROVAL,        // Notification sent to customer after approval
    ACCOUNT_REJECTION,       // Notification sent to customer after rejection
    NEW_MESSAGE,             // Received internal message (manager ↔ customer)
    SECURITY_ALERT,          // Suspicious login, password reset, etc.
    SYSTEM_UPDATE,           // App update or maintenance announcement
    CUSTOM,                  // Free-form event
    INFO,                    // Generic information
    WARNING,                 // Non-critical alert
    ERROR                    // Critical issue or operation failure
}
