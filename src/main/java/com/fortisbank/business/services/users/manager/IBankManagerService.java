package com.fortisbank.business.services.users.manager;

     import com.fortisbank.contracts.collections.ManagerList;
     import com.fortisbank.contracts.models.users.BankManager;

     /**
      * Interface for bank manager-related operations.
      */
     public interface IBankManagerService {

         /**
          * Creates a new bank manager.
          *
          * @param manager the bank manager to be created
          * @return the created bank manager
          */
         BankManager createManager(BankManager manager);

         /**
          * Updates the given bank manager.
          *
          * @param manager the bank manager to be updated
          * @return the updated bank manager
          */
         BankManager updateBankManager(BankManager manager);

         /**
          * Retrieves all bank managers.
          *
          * @return a list of all bank managers
          */
         ManagerList getAllManagers();

         /**
          * Searches for a bank manager by email.
          *
          * @param email the email of the bank manager
          * @return the found bank manager or null if not found
          */
         BankManager getManagerByEmail(String email);

         /**
          * Checks if an email is already in use by a bank manager.
          *
          * @param email the email to check
          * @return true if the email exists, false otherwise
          */
         boolean emailExists(String email);

         /**
          * Updates the security credentials (password and PIN) of a bank manager.
          *
          * @param manager the bank manager to be updated
          * @param newPassword the new password
          * @param newPin the new PIN
          */
         void updateManagerWithSecurity(BankManager manager, char[] newPassword, char[] newPin);
     }