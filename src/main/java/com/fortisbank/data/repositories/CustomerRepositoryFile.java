package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;

import java.io.File;
import java.util.List;

/**
 * Repository class for managing customer data stored in a file.
 * Extends the FileRepository class and implements the ICustomerRepository interface.
 */
public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

    private static final File file = new File("data/customers.ser"); // File to store customer data
    private static CustomerRepositoryFile instance; // Singleton instance

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the repository with the specified file.
     */
    private CustomerRepositoryFile() {
        super(file);
    }

    /**
     * Returns the singleton instance of CustomerRepositoryFile.
     * Synchronized to prevent multiple threads from creating multiple instances.
     *
     * @return the singleton instance of CustomerRepositoryFile
     */
    public static synchronized CustomerRepositoryFile getInstance() {
        if (instance == null) {
            instance = new CustomerRepositoryFile();
        }
        return instance;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the customer with the specified ID, or null if not found
     */
    @Override
    public Customer getCustomerById(String id) {
        return readAll().stream()
                .filter(c -> c.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserts a new customer into the file.
     *
     * @param customer the customer to insert
     */
    @Override
    public void insertCustomer(Customer customer) {
        List<Customer> customers = readAll();
        customers.add(customer);
        writeAll(customers);
    }

    /**
     * Updates an existing customer in the file.
     *
     * @param customer the customer to update
     */
    @Override
    public void updateCustomer(Customer customer) {
        var customers = readAll();
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getUserId().equals(customer.getUserId())) {
                customers.set(i, customer);
                break;
            }
        }
        writeAll(customers);
    }

    /**
     * Deletes a customer from the file.
     *
     * @param id the ID of the customer to delete
     */
    @Override
    public void deleteCustomer(String id) {
        var customers = readAll();
        customers.removeIf(c -> c.getUserId().equals(id));
        writeAll(customers);
    }

    /**
     * Retrieves all customers from the file.
     *
     * @return a list of all customers
     */
    @Override
    public CustomerList getAllCustomers() {
        return new CustomerList(readAll());
    }
}