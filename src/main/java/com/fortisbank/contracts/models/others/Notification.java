package com.fortisbank.contracts.models.others;

import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.services.users.customer.CustomerService;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.IdGenerator;
import com.fortisbank.data.dal_utils.StorageMode;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing a notification.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationId;
    private String recipientUserId; // NEW FIELD: actual recipient
    private NotificationType type;
    private String title;
    private String message;
    private Date timestamp;
    private boolean read;
    private Customer relatedCustomer;
    private Account relatedAccount;

    public Notification(NotificationType type, String title, String message) {
        this.notificationId = IdGenerator.generateId();
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = new Date();
        this.read = false;
    }

    public Notification(NotificationType type, String title, String message, Customer customer, Account account) {
        this(type, title, message);
        this.relatedCustomer = customer;
        this.relatedAccount = account;
        if (customer != null) {
            this.recipientUserId = customer.getUserId();
        }
    }

    public Notification(String notificationId, String recipientUserId, NotificationType type, String title,
                        String message, boolean seen, Date timestamp) {
        this.notificationId = notificationId;
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = seen;
        // relatedCustomer and relatedAccount can be set later, if applicable
    }

    public Notification(String notificationId, String recipientUserId, NotificationType type, String title,
                        String message, boolean seen, Date timestamp, Customer relatedCustomer, Account relatedAccount) {
        this(notificationId, recipientUserId, type, title, message, seen, timestamp);
        this.relatedCustomer = relatedCustomer;
        this.relatedAccount = relatedAccount;
    }


    // --- Getters ---

    public String getNotificationId() {
        return notificationId;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public Customer getRelatedCustomer() {
        return relatedCustomer;
    }

    public Account getRelatedAccount() {
        return relatedAccount;
    }

    // --- Setters ---

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setRelatedCustomer(Customer relatedCustomer) {
        this.relatedCustomer = relatedCustomer;
    }

    public void setRelatedAccount(Account relatedAccount) {
        this.relatedAccount = relatedAccount;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message + " (" + timestamp + ")";
    }
}
