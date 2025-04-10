package com.fortisbank.business.services.users.customer;

 import com.fortisbank.business.services.account.AccountService;
 import com.fortisbank.data.interfaces.ICustomerRepository;
 import com.fortisbank.data.dal_utils.RepositoryFactory;
 import com.fortisbank.data.dal_utils.StorageMode;
 import com.fortisbank.contracts.collections.AccountList;
 import com.fortisbank.contracts.collections.CustomerList;
 import com.fortisbank.contracts.models.users.Customer;
 import com.fortisbank.business.bll_utils.SecurityUtils;

 import java.util.EnumMap;
 import java.util.Map;
 import java.util.logging.Level;
 import java.util.logging.Logger;

 public class CustomerService implements ICustomerService {

     private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());
     private static final Map<StorageMode, CustomerService> instances = new EnumMap<>(StorageMode.class);

     private final ICustomerRepository customerRepository;
     private final AccountService accountService;

     private CustomerService(StorageMode storageMode) {
         RepositoryFactory repoFactory = RepositoryFactory.getInstance(storageMode);
         this.customerRepository = repoFactory.getCustomerRepository();
         this.accountService = AccountService.getInstance(storageMode);
     }

     public static synchronized CustomerService getInstance(StorageMode storageMode) {
         return instances.computeIfAbsent(storageMode, CustomerService::new);
     }

     @Override
     public void createCustomer(Customer customer) {
         validateNotNull(customer, "Customer");
         try {
             customerRepository.insertCustomer(customer);
             LOGGER.log(Level.INFO, "Customer created successfully: {0}", customer.getUserId());
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error creating customer: {0}", e.getMessage());
             throw new RuntimeException("Failed to create customer", e);
         }
     }

     @Override
     public void updateCustomer(Customer customer) {
         validateNotNull(customer, "Customer");
         try {
             customerRepository.updateCustomer(customer);
             LOGGER.log(Level.INFO, "Customer updated successfully: {0}", customer.getUserId());
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error updating customer: {0}", e.getMessage());
             throw new RuntimeException("Failed to update customer", e);
         }
     }

     @Override
     public void deleteCustomer(String id) {
         validateNotNull(id, "Customer ID");
         try {
             customerRepository.deleteCustomer(id);
             LOGGER.log(Level.INFO, "Customer deleted successfully: {0}", id);
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error deleting customer with ID: {0}", e.getMessage());
             throw new RuntimeException("Failed to delete customer", e);
         }
     }

     @Override
     public Customer getCustomer(String id) {
         validateNotNull(id, "Customer ID");
         try {
             Customer customer = customerRepository.getCustomerById(id);
             if (customer == null) {
                 LOGGER.log(Level.WARNING, "Customer not found with ID: {0}", id);
                 return null;
             }
             AccountList accounts = accountService.getAccountsByCustomerId(id);
             customer.setAccounts(accounts);
             LOGGER.log(Level.INFO, "Customer retrieved successfully: {0}", id);
             return customer;
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error retrieving customer with ID: {0}", e.getMessage());
             throw new RuntimeException("Failed to retrieve customer", e);
         }
     }

     @Override
     public CustomerList getAllCustomers() {
         try {
             CustomerList customers = customerRepository.getAllCustomers();
             for (Customer customer : customers) {
                 AccountList accounts = accountService.getAccountsByCustomerId(customer.getUserId());
                 customer.setAccounts(accounts);
             }
             LOGGER.log(Level.INFO, "All customers retrieved successfully");
             return customers;
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error retrieving all customers: {0}", e.getMessage());
             throw new RuntimeException("Failed to retrieve all customers", e);
         }
     }

     public boolean emailExists(String email) {
         validateNotNull(email, "Email");
         try {
             CustomerList allCustomers = customerRepository.getAllCustomers();
             for (Customer customer : allCustomers) {
                 if (customer.getEmail().equalsIgnoreCase(email)) {
                     LOGGER.log(Level.INFO, "Email already exists: {0}", email);
                     return true;
                 }
             }
             return false;
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error checking if email exists: {0}", e.getMessage());
             throw new RuntimeException("Failed to check email existence", e);
         }
     }

     public boolean phoneExists(String phoneNumber) {
         validateNotNull(phoneNumber, "Phone Number");
         try {
             CustomerList allCustomers = customerRepository.getAllCustomers();
             for (Customer customer : allCustomers) {
                 if (customer.getPhoneNumber().equals(phoneNumber)) {
                     LOGGER.log(Level.INFO, "Phone number already exists: {0}", phoneNumber);
                     return true;
                 }
             }
             return false;
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error checking if phone number exists: {0}", e.getMessage());
             throw new RuntimeException("Failed to check phone number existence", e);
         }
     }

     public void updateCustomerWithSecurity(Customer customer, char[] newPassword, char[] newPin) {
         validateNotNull(customer, "Customer");
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
             LOGGER.log(Level.INFO, "Customer security credentials updated successfully: {0}", customer.getUserId());
         } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error updating customer security credentials: {0}", e.getMessage());
             throw new RuntimeException("Failed to update customer security credentials", e);
         }
     }

     private void validateNotNull(Object obj, String fieldName) {
         if (obj == null) {
             LOGGER.log(Level.SEVERE, "{0} cannot be null", fieldName);
             throw new IllegalArgumentException(fieldName + " cannot be null");
         }
     }
 }