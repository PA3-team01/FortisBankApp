package com.fortisbank.business.services.manager;

 import com.fortisbank.business.services.account.AccountService;
 import com.fortisbank.data.repositories.IBankManagerRepository;
 import com.fortisbank.data.repositories.RepositoryFactory;
 import com.fortisbank.data.repositories.StorageMode;
 import com.fortisbank.models.accounts.Account;
 import com.fortisbank.models.collections.ManagerList;
 import com.fortisbank.models.users.BankManager;
 import com.fortisbank.models.users.Customer;
 import com.fortisbank.utils.SecurityUtils;

 import java.util.EnumMap;
 import java.util.Map;

 /**
  * Service class for managing bank managers.
  */
 public class BankManagerService implements IBankManagerService {

     private static final Map<StorageMode, BankManagerService> instances = new EnumMap<>(StorageMode.class);

     private final IBankManagerRepository managerRepository;
     private StorageMode storageMode;

     /**
      * Private constructor to prevent instantiation.
      *
      * @param storageMode the storage mode
      */
     private BankManagerService(StorageMode storageMode) {
         this.storageMode = storageMode;
         this.managerRepository = RepositoryFactory.getInstance(storageMode).getBankManagerRepository();
     }

     /**
      * Returns the singleton instance of BankManagerService for the given storage mode.
      *
      * @param storageMode the storage mode
      * @return the singleton instance of BankManagerService
      */
     public static synchronized BankManagerService getInstance(StorageMode storageMode) {
         return instances.computeIfAbsent(storageMode, BankManagerService::new);
     }

     // ------------------- CRUD OPERATIONS -------------------

     // Create

     /**
      * Creates a new bank manager.
      *
      * @param manager the manager to be created
      * @return the created manager
      */
     public BankManager createManager(BankManager manager) { // ** Do not call directly, use register service **
         managerRepository.insertManager(manager);
         return manager;
     }

     /**
      * Updates the given bank manager.
      *
      * @param manager the manager to be updated
      * @return the updated manager
      */
     public BankManager updateBankManager(BankManager manager) {
         managerRepository.updateManager(manager);
         return manager;
     }

     // ------------------- Core Business Methods -------------------

     /**
      * Creates a new customer.
      *
      * @param firstName the first name of the customer
      * @param lastName the last name of the customer
      * @param email the email of the customer
      * @param phone the phone number of the customer
      * @param hashedPassword the hashed password of the customer
      * @param pinHash the hashed PIN of the customer
      * @return the created customer
      */
     @Override
     public Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash) {
         // Implementation depends on whether manager creates users directly or uses RegisterService
         //TODO: Implement this method
         return null;
     }

     /**
      * Approves an account.
      *
      * @param account the account to be approved
      * @return true if the account is approved
      */
     @Override
     public boolean approveAccount(Account account) {
         account.setActive(true);
         return true;
     }

     /**
      * Closes an account.
      *
      * @param account the account to be closed
      * @return true if the account is successfully closed
      */
     @Override
     public boolean closeAccount(Account account) {
         try {
             if (account == null) {
                 throw new IllegalStateException("Account cannot be null");
             }
             if (!account.isActive()) {
                 throw new IllegalStateException("Account is already closed");
             }
             AccountService.getInstance(storageMode).closeAccount(account);

             return true;
         } catch (IllegalStateException e) {
             System.err.println("Failed to close account: " + e.getMessage());
             return false;
         }
     }

     /**
      * Deletes a customer.
      *
      * @param customer the customer to be deleted
      * @return true if the customer is successfully deleted
      */
     @Override
     public boolean deleteCustomer(Customer customer) {
         // You can delegate to CustomerService or remove via repository directly
         return false;
     }

     /**
      * Generates a customer report.
      */
     @Override
     public void generateCustomerReport() {
         // Placeholder — would generate a report using available data (e.g., PDF, export)
     }

     // ------------------- Login Support -------------------

     /**
      * Retrieves all managers.
      *
      * @return the list of all managers
      */
     public ManagerList getAllManagers() {
         return managerRepository.getAllManagers();
     }

     /**
      * Searches for a manager by email.
      *
      * @param email the email of the manager
      * @return the found manager or null
      */
     public BankManager getManagerByEmail(String email) {
         for (BankManager manager : getAllManagers()) {
             if (manager.getEmail().equalsIgnoreCase(email)) {
                 return manager;
             }
         }
         return null;
     }

     // ------------------- Utility Methods -------------------

     /**
      * Checks if an email is already in use.
      *
      * @param email the email to check
      * @return true if the email already exists
      */
     public boolean emailExists(String email) {
         return getAllManagers().stream().anyMatch(manager -> manager.getEmail().equalsIgnoreCase(email));
     }

     /**
      * Updates the manager's security credentials (password and PIN).
      *
      * @param manager the manager to be updated
      * @param newPassword the new password
      * @param newPin the new PIN
      */
     public void updateManagerWithSecurity(BankManager manager, char[] newPassword, char[] newPin) {
         try {
             if (newPassword != null && newPassword.length > 0) {
                 manager.setHashedPassword(SecurityUtils.hashPassword(newPassword));
             }
             if (newPin != null && newPin.length > 0) {
                 manager.setPINHash(SecurityUtils.hashPIN(newPin));
             }
             // Standard info update
             updateBankManager(manager);
         } catch (Exception e) {
             throw new RuntimeException("Failed to update manager security fields", e);
         }
     }

 }