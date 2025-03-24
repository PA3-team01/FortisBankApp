package com.fortisbank.data.repositories;

import com.fortisbank.models.Customer;

import java.util.List;

public interface ICustomerRepository {
    Customer getCustomerById(String customerId);

    List<Customer> getAllCustomers();

    void insertCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(String customerId);
}
