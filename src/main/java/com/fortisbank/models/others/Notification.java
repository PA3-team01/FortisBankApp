package com.fortisbank.models.others;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private NotificationType type;
    private String title;
    private String message;
    private Date timestamp;
    private boolean read;

    // Optional references for contextual logic
    private Customer relatedCustomer;
    private Account relatedAccount;

    public Notification(NotificationType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = new Date();
        this.read = false;
    }

    // Constructor for notifications with customer and account context
    public Notification(NotificationType type, String title, String message, Customer customer, Account account) {
        this(type, title, message);
        this.relatedCustomer = customer;
        this.relatedAccount = account;
        this.timestamp = new Date();
        this.read = false;
    }

    // --- Getters ---
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

    // --- Mark as read ---
    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message + " (" + timestamp + ")";
    }
}
