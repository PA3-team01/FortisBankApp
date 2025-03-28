package com.fortisbank.business.services;

import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.CustomerList;

public interface ICustomerService {
    void createCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(String id);
    Customer getCustomer(String id);
    CustomerList getAllCustomers();
}