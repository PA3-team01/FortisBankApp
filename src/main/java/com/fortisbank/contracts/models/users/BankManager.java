package com.fortisbank.contracts.models.users;

import com.fortisbank.contracts.utils.IdGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a bank manager.
 */
public class BankManager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * List of customers managed by the bank manager.
     */
    private final List<Customer> customers;

    // ------------------------- CONSTRUCTORS -------------------------

    /**
     * Default constructor initializing a bank manager with an empty list of customers.
     */
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

    /**
     * Constructor initializing a bank manager with specified values.
     *
     * @param firstName the first name of the bank manager
     * @param lastName the last name of the bank manager
     * @param email the email of the bank manager
     * @param hashedPassword the hashed password of the bank manager
     * @param pinHash the hashed PIN of the bank manager
     */
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

    /**
     * Sets the list of customers managed by the bank manager.
     *
     * @param customers the list of customers
     */
    public void setCustomers(List<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }

    /**
     * Adds a customer to the list of customers managed by the bank manager.
     *
     * @param customer the customer to add
     */
    public void addCustomer(Customer customer) {
        this.customers.add(customer);
    }

    /**
     * Removes a customer from the list of customers managed by the bank manager.
     *
     * @param customer the customer to remove
     */
    public void removeCustomer(Customer customer) {
        this.customers.remove(customer);
    }

    // ------------------------- GETTERS -------------------------

    /**
     * Returns the list of customers managed by the bank manager.
     *
     * @return the list of customers
     */
    public List<Customer> getCustomers() {
        return customers;
    }

    // ------------------------- TO STRING -------------------------

    /**
     * Returns a string representation of the bank manager.
     *
     * @return a string representation of the bank manager
     */
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