package com.fortisbank.models.users;

import java.io.Serializable;

public abstract class User implements Serializable {

    protected String userId;
    protected String email;
    protected String hashedPassword; // application password
    protected String PINHash; // PIN hash for authentication
    protected String firstName;
    protected String lastName;
    protected Role role;

    public enum Role {
        CUSTOMER,
        MANAGER
    }

    // ------------------- Getters -------------------

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

    // ------------------- Setters -------------------

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

    // ------------------- Debug -------------------

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
