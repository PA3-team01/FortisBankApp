package com.fortisbank.business.services.customer;

import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;

public interface ICustomerService {
    void createCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(String id);
    Customer getCustomer(String id);
    CustomerList getAllCustomers();
}