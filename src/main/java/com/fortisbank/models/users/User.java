package com.fortisbank.models.users;

import com.fortisbank.models.others.Notification;

import java.io.Serializable;
import java.util.List;

public abstract class User implements Serializable {

    protected String userId;
    protected String email;
    protected String hashedPassword;
    protected String PINHash;
    protected String firstName;
    protected String lastName;
    protected Role role;

    // inbox (list of Notifications in models/others)
    protected List<Notification> inbox;



    protected User(String userId, String firstName, String lastName, String email,
                   String hashedPassword, String pinHash, Role role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.PINHash = pinHash;
        this.role = role;
        this.inbox = List.of(); // Initialize inbox as an empty list
    }

    protected User() {
        // No-arg constructor for deserialization
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getPINHash() {
        return PINHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setPINHash(String PINHash) {
        this.PINHash = PINHash;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public List<Notification> getInbox() {
        return inbox;
    }
    public void setInbox(List<Notification> inbox) {
        this.inbox = inbox;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}

