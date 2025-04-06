package com.fortisbank.business.services.customer;

import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;

/**
 * Interface for customer-related operations.
 */
public interface ICustomerService {

    /**
     * Creates a new customer.
     *
     * @param customer the customer to be created
     */
    void createCustomer(Customer customer);

    /**
     * Updates the given customer.
     *
     * @param customer the customer to be updated
     */
    void updateCustomer(Customer customer);

    /**
     * Deletes the customer with the given ID.
     *
     * @param id the ID of the customer to be deleted
     */
    void deleteCustomer(String id);

    /**
     * Retrieves the customer with the given ID.
     *
     * @param id the ID of the customer to be retrieved
     * @return the customer with the given ID
     */
    Customer getCustomer(String id);

    /**
     * Retrieves all customers.
     *
     * @return the list of all customers
     */
    CustomerList getAllCustomers();
}