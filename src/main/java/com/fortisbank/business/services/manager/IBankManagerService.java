package com.fortisbank.business.services.manager;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;

public interface IBankManagerService {
    Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash);
    boolean approveAccount(Account account);
    boolean closeAccount(Account account);
    boolean deleteCustomer(Customer customer);
    void generateCustomerReport();
}

