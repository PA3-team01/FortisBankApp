package com.fortisbank.business.services;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;

public class BankManagerService implements IBankManagerService {
    @Override
    public Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash) {
        return null;
    }

    @Override
    public boolean approveAccount(Account account) {
        return false;
    }

    @Override
    public boolean closeAccount(Account account) {
        return false;
    }

    @Override
    public boolean deleteCustomer(Customer customer) {
        return false;
    }

    @Override
    public void generateCustomerReport() {

    }
}
