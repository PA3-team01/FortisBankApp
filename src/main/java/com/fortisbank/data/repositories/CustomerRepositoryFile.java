package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.collections.CustomerList;

import java.io.File;
import java.util.List;

public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

    private static final File file = new File("data/customers.ser");
    private static CustomerRepositoryFile instance;

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
    public Customer getCustomerById(String id) {
        return readAll().stream()
                .filter(c -> c.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertCustomer(Customer customer) {
        List<Customer> customers = readAll();
        customers.add(customer);
        writeAll(customers);
    }

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

    @Override
    public void deleteCustomer(String id) {
        var customers = readAll();
        customers.removeIf(c -> c.getUserId().equals(id));
        writeAll(customers);
    }

    @Override
    public CustomerList getAllCustomers() {
        return (CustomerList) readAll();
    }
}