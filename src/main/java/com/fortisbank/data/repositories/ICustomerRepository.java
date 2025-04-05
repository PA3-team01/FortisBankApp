package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;

public interface ICustomerRepository {
    Customer getCustomerById(String customerId);

    CustomerList getAllCustomers();

    void insertCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(String customerId);
}
