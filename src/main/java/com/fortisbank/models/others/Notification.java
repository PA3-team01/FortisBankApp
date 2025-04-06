package com.fortisbank.models.others;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing a notification.
 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The type of the notification.
     */
    private NotificationType type;

    /**
     * The title of the notification.
     */
    private String title;

    /**
     * The message of the notification.
     */
    private String message;

    /**
     * The timestamp when the notification was created.
     */
    private Date timestamp;

    /**
     * Indicates whether the notification has been read.
     */
    private boolean read;

    /**
     * The customer related to the notification, if any.
     */
    private Customer relatedCustomer;

    /**
     * The account related to the notification, if any.
     */
    private Account relatedAccount;

    /**
     * Constructor initializing the notification with type, title, and message.
     *
     * @param type the type of the notification
     * @param title the title of the notification
     * @param message the message of the notification
     */
    public Notification(NotificationType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = new Date();
        this.read = false;
    }

    /**
     * Constructor initializing the notification with type, title, message, customer, and account.
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
        this.timestamp = new Date();
        this.read = false;
    }

    // --- Getters ---

    /**
     * Returns the type of the notification.
     *
     * @return the notification type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Returns the title of the notification.
     *
     * @return the notification title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the message of the notification.
     *
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the timestamp when the notification was created.
     *
     * @return the notification timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Returns whether the notification has been read.
     *
     * @return true if the notification has been read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Returns the customer related to the notification.
     *
     * @return the related customer
     */
    public Customer getRelatedCustomer() {
        return relatedCustomer;
    }

    /**
     * Returns the account related to the notification.
     *
     * @return the related account
     */
    public Account getRelatedAccount() {
        return relatedAccount;
    }

    // --- Setters ---

    /**
     * Sets the type of the notification.
     *
     * @param type the notification type to set
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Sets the title of the notification.
     *
     * @param title the notification title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the message of the notification.
     *
     * @param message the notification message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets whether the notification has been read.
     *
     * @param read true to mark the notification as read, false otherwise
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Sets the customer related to the notification.
     *
     * @param relatedCustomer the related customer to set
     */
    public void setRelatedCustomer(Customer relatedCustomer) {
        this.relatedCustomer = relatedCustomer;
    }

    /**
     * Sets the account related to the notification.
     *
     * @param relatedAccount the related account to set
     */
    public void setRelatedAccount(Account relatedAccount) {
        this.relatedAccount = relatedAccount;
    }

    // --- Mark as read ---

    /**
     * Marks the notification as read.
     */
    public void markAsRead() {
        this.read = true;
    }

    /**
     * Returns a string representation of the notification.
     *
     * @return a string containing notification information
     */
    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message + " (" + timestamp + ")";
    }
}