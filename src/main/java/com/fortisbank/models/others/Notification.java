package com.fortisbank.models.others;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private NotificationType type;
    private String title;
    private String message;
    private Date timestamp;
    private boolean read;

    public Notification(NotificationType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
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

    // --- Mark as read ---
    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + title + " - " + message + " (" + timestamp + ")";
    }
}
