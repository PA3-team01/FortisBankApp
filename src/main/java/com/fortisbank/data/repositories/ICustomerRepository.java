package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;

/**
 * Interface for customer repository operations.
 * Provides methods to manage customer data.
 */
public interface ICustomerRepository {

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the customer with the specified ID, or null if not found
     */
    Customer getCustomerById(String customerId);

    /**
     * Retrieves all customers.
     *
     * @return a list of all customers
     */
    CustomerList getAllCustomers();

    /**
     * Inserts a new customer.
     *
     * @param customer the customer to insert
     */
    void insertCustomer(Customer customer);

    /**
     * Updates an existing customer.
     *
     * @param customer the customer to update
     */
    void updateCustomer(Customer customer);

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId the ID of the customer to delete
     */
    void deleteCustomer(String customerId);
}