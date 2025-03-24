package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.CustomerList;

import java.io.File;
import java.util.List;

/**
 * CustomerRepositoryFile is an implementation of the ICustomerRepository interface
 * that handles the persistence of customer data using file storage.
 * This class extends the generic FileRepository to provide specialized functionality
 * for Customer objects.
 */
public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

    public CustomerRepositoryFile() {
        super(new File("data/customers.ser"));
    }

    /**
     * Retrieves a customer by their unique ID.
     *
     * @param id the unique identifier of the customer to retrieve
     * @return the Customer object with the specified ID, or null if no customer is found
     */
    @Override
    public Customer getCustomerById(String id) {
        return readAll().stream()
                .filter(c -> c.getCustomerID().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserts a new customer into the repository. The customer is added to the
     * existing list of customers, and the updated list is saved back to the
     * storage medium.
     *
     * @param customer the Customer object to be inserted into the repository
     */
    @Override
    public void insertCustomer(Customer customer) {
        List<Customer> customers = readAll();
        customers.add(customer);
        writeAll(customers);
    }

    /**
     * Updates an existing customer's information in the repository. The method searches
     * for a customer in the repository with the same unique customer ID as the given
     * Customer object. If a match is found, the customer's information is replaced
     * with the new details provided.
     *
     * @param customer the Customer object containing updated information, including the
     *                 customer's unique ID to identify the record to be updated
     */
    @Override
    public void updateCustomer(Customer customer) {
        var customers = readAll();
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerID().equals(customer.getCustomerID())) {
                customers.set(i, customer);
                break;
            }
        }
        writeAll(customers);
    }

    /**
     * Deletes a customer from the repository based on their unique ID. The method
     * removes the customer with the specified identifier from the current list of
     * customers and saves the updated list back to the storage medium.
     *
     * @param id the unique identifier of the customer to be deleted
     */
    @Override
    public void deleteCustomer(String id) {
        var customers = readAll();
        customers.removeIf(c -> c.getCustomerID().equals(id));
        writeAll(customers);
    }

    /**
     * Retrieves all customers from the repository.
     *
     * @return a CustomerList containing all the customers fetched from the file storage
     */
    @Override
    public CustomerList getAllCustomers() {
        return (CustomerList) readAll();
    }
}
