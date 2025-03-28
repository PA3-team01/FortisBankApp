package com.fortisbank.business.services;

    import com.fortisbank.data.repositories.ICustomerRepository;
    import com.fortisbank.data.repositories.RepositoryFactory;
    import com.fortisbank.data.repositories.StorageMode;
    import com.fortisbank.models.Customer;
    import com.fortisbank.models.collections.CustomerList;

    public class CustomerService {
        private static CustomerService instance;
        private final RepositoryFactory repoFactory;
        private final ICustomerRepository customerRepository;

        private CustomerService(StorageMode storageMode) {
            this.repoFactory = RepositoryFactory.getInstance(storageMode);
            this.customerRepository = repoFactory.getCustomerRepository();
        }

        public static synchronized CustomerService getInstance(StorageMode storageMode) {
            if (instance == null) {
                instance = new CustomerService(storageMode);
            }
            return instance;
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