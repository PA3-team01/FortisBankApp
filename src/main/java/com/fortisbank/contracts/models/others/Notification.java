package com.fortisbank.contracts.models.others;

import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.IdGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Class representing a notification.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the notification.
     */
    private String notificationId;

    private NotificationType type;
    private String title;
    private String message;
    private Date timestamp;
    private boolean read;
    private Customer relatedCustomer;
    private Account relatedAccount;

    /**
     * Constructor initializing the notification with type, title, and message.
     *
     * @param type the type of the notification
     * @param title the title of the notification
     * @param message the message of the notification
     */
    public Notification(NotificationType type, String title, String message) {
        this.notificationId = IdGenerator.generateId();
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = new Date();
        this.read = false;
    }

    /**
     * Constructor initializing the notification with related entities.
     *
     * @param type the type of the notification
     * @param title the title of the notification
     * @param message the message of the notification
     * @param customer the customer related to the notification
     * @param account the account related to the notification
     */
    public Notification(NotificationType type, String title, String message, Customer customer, Account account) {
        this(type, title, message);
        this.relatedCustomer = customer;
        this.relatedAccount = account;
    }

    // --- Getters ---

    public String getNotificationId() {
        return notificationId;
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

    public void markAsRead() {
        this.read = true;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message + " (" + timestamp + ")";
    }
}
