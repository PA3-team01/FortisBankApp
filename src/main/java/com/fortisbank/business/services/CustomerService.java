package com.fortisbank.business.services;

import com.fortisbank.data.repositories.ICustomerRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.CustomerList;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service class responsible for managing customer-related operations
 * with support for multiple storage modes (FILE or DATABASE).
 *
 * This version enriches customer objects with their accounts from AccountService.
 */
public class CustomerService implements ICustomerService {

    private static final Map<StorageMode, CustomerService> instances = new EnumMap<>(StorageMode.class);

    private final ICustomerRepository customerRepository;
    private final AccountService accountService;

    // ------------------- Constructor & Singleton -------------------

    private CustomerService(StorageMode storageMode) {
        RepositoryFactory repoFactory = RepositoryFactory.getInstance(storageMode);
        this.customerRepository = repoFactory.getCustomerRepository();
        this.accountService = AccountService.getInstance(storageMode);
    }

    public static synchronized CustomerService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, CustomerService::new);
    }

    // ------------------- CRUD Operations -------------------

    @Override
    public void createCustomer(Customer customer) {
        customerRepository.insertCustomer(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerRepository.updateCustomer(customer);
    }

    @Override
    public void deleteCustomer(String id) {
        customerRepository.deleteCustomer(id);
    }

    @Override
    public Customer getCustomer(String id) {
        Customer customer = customerRepository.getCustomerById(id);
        AccountList accounts = accountService.getAccountsByCustomerId(id);
        customer.setAccounts(accounts);
        return customer;
    }

    @Override
    public CustomerList getAllCustomers() {
        CustomerList customers = customerRepository.getAllCustomers();
        for (Customer customer : customers) {
            AccountList accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            customer.setAccounts(accounts);
        }
        return customers;
    }
}
