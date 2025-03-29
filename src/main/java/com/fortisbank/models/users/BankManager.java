package com.fortisbank.models.users;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.CheckingAccount;
import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankManager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Customer> customers;

    public BankManager(String firstName, String lastName, String email, String hashedPassword, String pinHash) {
        this.userId = IdGenerator.generateId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.PINHash = pinHash;
        this.role = Role.MANAGER;
        this.customers = new ArrayList<>();
    }

    // ------------------------- SETTERS -------------------------
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public void setPINHash(String pinHash) {
        this.PINHash = pinHash;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }
    public void addCustomer(Customer customer) {
        this.customers.add(customer);
    }
    public void removeCustomer(Customer customer) {
        this.customers.remove(customer);
    }
    // ------------------------- GETTERS -------------------------
    public String getUserId() {
        return userId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
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
    public List<Customer> getCustomers() {
        return customers;
    }

    // ------------------------- toString -------------------------
    @Override
    public String toString() {
        return "BankManager{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", customersCount=" + customers.size() +
                '}';
    }
}
