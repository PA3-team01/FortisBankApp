package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.CustomerList;

import java.util.List;

public class CustomerRepositoryFile extends FileRepository<Customer> implements ICustomerRepository {

    public CustomerRepositoryFile() {
        super("data/customers.ser");
    }

    @Override
    public Customer getCustomerById(String id) {
        return readAll().stream()
                .filter(c -> c.getCustomerID().equals(id))
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
            if (customers.get(i).getCustomerID().equals(customer.getCustomerID())) {
                customers.set(i, customer);
                break;
            }
        }
        writeAll(customers);
    }

    @Override
    public void deleteCustomer(String id) {
        var customers = readAll();
        customers.removeIf(c -> c.getCustomerID().equals(id));
        writeAll(customers);
    }

    @Override
    public CustomerList getAllCustomers() {
        return (CustomerList) readAll();
    }
}
