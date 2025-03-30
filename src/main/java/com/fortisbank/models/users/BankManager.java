package com.fortisbank.models.users;

import com.fortisbank.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankManager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Customer> customers;

    // ------------------------- CONSTRUCTORS -------------------------

    public BankManager() {
        super(
                IdGenerator.generateId(),
                "",
                "",
                "",
                "",
                "",
                Role.MANAGER
        );
        this.customers = new ArrayList<>();
    }

    public BankManager(String firstName, String lastName, String email, String hashedPassword, String pinHash) {
        super(
                IdGenerator.generateId(),
                firstName,
                lastName,
                email,
                hashedPassword,
                pinHash,
                Role.MANAGER
        );
        this.customers = new ArrayList<>();
    }

    // ------------------------- SETTERS -------------------------

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

    public List<Customer> getCustomers() {
        return customers;
    }

    // ------------------------- TO STRING -------------------------

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
