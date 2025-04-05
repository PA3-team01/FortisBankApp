package com.fortisbank.business.services.customer;

import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.data.repositories.ICustomerRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.models.users.Customer;
import com.fortisbank.utils.SecurityUtils;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service class responsible for managing customer-related operations
 * with support for multiple storage modes (FILE or DATABASE).
 *
 * This version enriches customer objects with their accounts from AccountService. ***
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
        Customer customer = customerRepository.getCustomerById(id); // fetch customer by ID (account list is empty at this point
        AccountList accounts = accountService.getAccountsByCustomerId(id);// fetch accounts by customer ID
        customer.setAccounts(accounts); // set accounts to customer
        return customer;
    }

    @Override
    public CustomerList getAllCustomers() {
        CustomerList customers = customerRepository.getAllCustomers();
        // as accounts are stored in a different repository, we need to fetch them separately
        // and set them to each customer
        for (Customer customer : customers) {
            AccountList accounts = accountService.getAccountsByCustomerId(customer.getUserId());
            customer.setAccounts(accounts);
        }
        return customers;
    }

    // ------------------- Additional Methods -------------------
    /**
     * Checks if a customer with the given email already exists.
     *
     * @param email the email to check
     * @return true if the email is already used, false otherwise
     */
    public boolean emailExists(String email) {
        CustomerList allCustomers = customerRepository.getAllCustomers();
        for (Customer customer : allCustomers) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a customer with the given phone number already exists.
     *
     * @param phoneNumber the phone number to check
     * @return true if the phone number is already used, false otherwise
     */
    public boolean phoneExists(String phoneNumber) {
        CustomerList allCustomers = customerRepository.getAllCustomers();
        for (Customer customer : allCustomers) {
            if (customer.getPhoneNumber().equals(phoneNumber)) {
                return true;
            }
        }
        return false;
    }


    public void updateCustomerWithSecurity(Customer customer, char[] newPassword, char[] newPin) {
        try {
            if (newPassword != null && newPassword.length > 0) {
                String hashedPassword = SecurityUtils.hashPassword(newPassword);
                customer.setHashedPassword(hashedPassword);
            }

            if (newPin != null && newPin.length > 0) {
                String hashedPin = SecurityUtils.hashPIN(newPin);
                customer.setPINHash(hashedPin);
            }

            updateCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update customer credentials: " + e.getMessage(), e);
        }
    }

}
