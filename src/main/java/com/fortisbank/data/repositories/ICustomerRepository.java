package com.fortisbank.data.repositories;

import com.fortisbank.models.users.Customer;
import com.fortisbank.models.collections.CustomerList;

public interface ICustomerRepository {
    Customer getCustomerById(String customerId);

    CustomerList getAllCustomers();

    void insertCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(String customerId);
}
