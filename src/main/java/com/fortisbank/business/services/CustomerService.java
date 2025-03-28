package com.fortisbank.business.services;

import com.fortisbank.data.repositories.ICustomerRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.Customer;
import com.fortisbank.models.collections.CustomerList;

import java.util.EnumMap;
import java.util.Map;

public class CustomerService implements ICustomerService {
    private static final Map<StorageMode, CustomerService> instances = new EnumMap<>(StorageMode.class);
    private final RepositoryFactory repoFactory;
    private final ICustomerRepository customerRepository;

    /**
     * The CustomerService class is designed to manage customer-related operations
     * using different storage modes (FILE or DATABASE). It follows the Singleton
     * pattern to ensure that only one instance of CustomerService exists per StorageMode.
     *
     * - The `instances` map holds CustomerService instances, keyed by StorageMode.
     * - The constructor is private to prevent direct instantiation and initializes
     *   the repository factory and customer repository based on the given storage mode.
     * - The `getInstance` method returns the existing instance for the given StorageMode
     *   or creates a new one if it doesn't exist.
     * - The CRUD methods (createCustomer, updateCustomer, deleteCustomer, getCustomer,
     *   getAllCustomers) delegate operations to the appropriate repository based on
     *   the current storage mode.
     *
     * This design allows the application to switch between different storage modes
     * seamlessly while ensuring that each storage mode has its own dedicated service
     * instance.
     */
    private CustomerService(StorageMode storageMode) {
        this.repoFactory = RepositoryFactory.getInstance(storageMode);
        this.customerRepository = repoFactory.getCustomerRepository();
    }

    public static synchronized CustomerService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, CustomerService::new);
    }

    public void createCustomer(Customer customer) {
        customerRepository.insertCustomer(customer);
    }

    public void updateCustomer(Customer customer) {
        customerRepository.updateCustomer(customer);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteCustomer(id);
    }

    public Customer getCustomer(String id) {
        return customerRepository.getCustomerById(id);
    }

    public CustomerList getAllCustomers() {
        return customerRepository.getAllCustomers();
    }
}