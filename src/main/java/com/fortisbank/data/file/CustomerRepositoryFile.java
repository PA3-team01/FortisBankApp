package com.fortisbank.data.file;

import com.fortisbank.contracts.collections.CustomerList;
import com.fortisbank.contracts.exceptions.CustomerRepositoryException;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.data.interfaces.ICustomerRepository;
import com.fortisbank.data.dal_utils.NotificationRepositoryException;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

    private static final File file = new File("data/customers.ser");
    private static CustomerRepositoryFile instance;
    private static final Logger LOGGER = Logger.getLogger(CustomerRepositoryFile.class.getName());

    private CustomerRepositoryFile() {
        super(file);
    }

    public static synchronized CustomerRepositoryFile getInstance() {
        if (instance == null) {
            instance = new CustomerRepositoryFile();
        }
        return instance;
    }

    @Override
    public Customer getCustomerById(String id) throws CustomerRepositoryException {
        try {
            List<Customer> customers = readAll();
            Customer customer = customers.stream()
                    .filter(c -> c.getUserId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new CustomerRepositoryException("Customer with ID " + id + " not found."));
            try {
                customer.setInbox(NotificationRepositoryFile.getInstance().getNotificationsByUserId(id));
            } catch (NotificationRepositoryException e) {
                LOGGER.log(Level.WARNING, "Failed to load inbox for customer " + id, e);
            }
            return customer;
        } catch (Exception e) {
            throw new CustomerRepositoryException("Error retrieving customer with ID: " + id, e);
        }
    }

    @Override
    public void insertCustomer(Customer customer) throws CustomerRepositoryException {
        try {
            List<Customer> customers = readAll();
            customers.add(customer);
            writeAll(customers);
        } catch (Exception e) {
            throw new CustomerRepositoryException("Error inserting customer", e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws CustomerRepositoryException {
        try {
            var customers = readAll();
            boolean updated = false;
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getUserId().equals(customer.getUserId())) {
                    customers.set(i, customer);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                throw new CustomerRepositoryException("Customer with ID " + customer.getUserId() + " not found for update.");
            }
            writeAll(customers);
        } catch (Exception e) {
            throw new CustomerRepositoryException("Error updating customer", e);
        }
    }

    @Override
    public void deleteCustomer(String id) throws CustomerRepositoryException {
        try {
            var customers = readAll();
            boolean removed = customers.removeIf(c -> c.getUserId().equals(id));
            if (!removed) {
                throw new CustomerRepositoryException("Customer with ID " + id + " not found for deletion.");
            }
            writeAll(customers);
        } catch (Exception e) {
            throw new CustomerRepositoryException("Error deleting customer with ID: " + id, e);
        }
    }

    @Override
    public CustomerList getAllCustomers() throws CustomerRepositoryException {
        try {
            List<Customer> customers = readAll();
            for (Customer customer : customers) {
                try {
                    customer.setInbox(NotificationRepositoryFile.getInstance().getNotificationsByUserId(customer.getUserId()));
                } catch (NotificationRepositoryException e) {
                    LOGGER.log(Level.WARNING, "Failed to load inbox for customer " + customer.getUserId(), e);
                }
            }
            return new CustomerList(customers);
        } catch (Exception e) {
            throw new CustomerRepositoryException("Error retrieving all customers", e);
        }
    }
}
