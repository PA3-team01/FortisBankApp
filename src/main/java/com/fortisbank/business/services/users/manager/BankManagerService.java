package com.fortisbank.business.services.users.manager;

  import com.fortisbank.business.services.account.AccountService;
  import com.fortisbank.business.services.users.customer.CustomerService;
  import com.fortisbank.data.interfaces.IBankManagerRepository;
  import com.fortisbank.data.dal_utils.RepositoryFactory;
  import com.fortisbank.data.dal_utils.StorageMode;
  import com.fortisbank.contracts.models.accounts.Account;
  import com.fortisbank.contracts.collections.ManagerList;
  import com.fortisbank.contracts.models.users.BankManager;
  import com.fortisbank.contracts.models.users.Customer;
  import com.fortisbank.business.bll_utils.SecurityUtils;

  import java.util.EnumMap;
  import java.util.Map;
  import java.util.logging.Level;
  import java.util.logging.Logger;

  /**
   * Service class for managing bank managers.
   */
  public class BankManagerService implements IBankManagerService {

      private static final Logger LOGGER = Logger.getLogger(BankManagerService.class.getName());
      private static final Map<StorageMode, BankManagerService> instances = new EnumMap<>(StorageMode.class);

      private final IBankManagerRepository managerRepository;
      private final CustomerService customerService;
      private final StorageMode storageMode;

      private BankManagerService(StorageMode storageMode) {
          this.storageMode = storageMode;
          this.managerRepository = RepositoryFactory.getInstance(storageMode).getBankManagerRepository();
          this.customerService = CustomerService.getInstance(storageMode);
      }

      public static synchronized BankManagerService getInstance(StorageMode storageMode) {
          return instances.computeIfAbsent(storageMode, BankManagerService::new);
      }

      // ------------------- CRUD OPERATIONS -------------------

      public BankManager createManager(BankManager manager) {
          validateNotNull(manager, "Manager");
          try {
              managerRepository.insertManager(manager);
              LOGGER.log(Level.INFO, "Manager created successfully: {0}", manager.getEmail());
              return manager;
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error creating manager: {0}", e.getMessage());
              throw new RuntimeException("Failed to create manager", e);
          }
      }


      public BankManager updateBankManager(BankManager manager) {
          validateNotNull(manager, "Manager");
          try {
              managerRepository.updateManager(manager);
              LOGGER.log(Level.INFO, "Manager updated successfully: {0}", manager.getEmail());
              return manager;
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error updating manager: {0}", e.getMessage());
              throw new RuntimeException("Failed to update manager", e);
          }
      }

      // ------------------- Login Support -------------------

      public ManagerList getAllManagers() {
          try {
              ManagerList managers = managerRepository.getAllManagers();
              LOGGER.log(Level.INFO, "All managers retrieved successfully.");
              return managers;
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error retrieving all managers: {0}", e.getMessage());
              throw new RuntimeException("Failed to retrieve all managers", e);
          }
      }

      public BankManager getManagerByEmail(String email) {
          validateNotNull(email, "Email");
          try {
              for (BankManager manager : getAllManagers()) {
                  if (manager.getEmail().equalsIgnoreCase(email)) {
                      LOGGER.log(Level.INFO, "Manager found with email: {0}", email);
                      return manager;
                  }
              }
              LOGGER.log(Level.WARNING, "No manager found with email: {0}", email);
              return null;
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error searching for manager by email: {0}", e.getMessage());
              throw new RuntimeException("Failed to search for manager by email", e);
          }
      }

      // ------------------- Utility Methods -------------------

      public boolean emailExists(String email) {
          validateNotNull(email, "Email");
          try {
              boolean exists = getAllManagers().stream().anyMatch(manager -> manager.getEmail().equalsIgnoreCase(email));
              LOGGER.log(Level.INFO, "Email exists check for {0}: {1}", new Object[]{email, exists});
              return exists;
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error checking if email exists: {0}", e.getMessage());
              throw new RuntimeException("Failed to check email existence", e);
          }
      }

      public void updateManagerWithSecurity(BankManager manager, char[] newPassword, char[] newPin) {
          validateNotNull(manager, "Manager");
          try {
              if (newPassword != null && newPassword.length > 0) {
                  manager.setHashedPassword(SecurityUtils.hashPassword(newPassword));
              }
              if (newPin != null && newPin.length > 0) {
                  manager.setPINHash(SecurityUtils.hashPIN(newPin));
              }
              updateBankManager(manager);
              LOGGER.log(Level.INFO, "Manager security credentials updated successfully: {0}", manager.getEmail());
          } catch (Exception e) {
              LOGGER.log(Level.SEVERE, "Error updating manager security credentials: {0}", e.getMessage());
              throw new RuntimeException("Failed to update manager security credentials", e);
          }
      }

      private void validateNotNull(Object obj, String fieldName) {
          if (obj == null) {
              LOGGER.log(Level.SEVERE, "{0} cannot be null", fieldName);
              throw new IllegalArgumentException(fieldName + " cannot be null");
          }
      }
  }