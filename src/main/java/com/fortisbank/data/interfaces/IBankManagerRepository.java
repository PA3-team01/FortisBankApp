package com.fortisbank.data.interfaces;

     import com.fortisbank.contracts.exceptions.BankManagerRepositoryException;
     import com.fortisbank.contracts.collections.ManagerList;
     import com.fortisbank.contracts.models.users.BankManager;

     /**
      * Interface for bank manager repository operations.
      * Provides methods to manage bank manager data.
      */
     public interface IBankManagerRepository {

         /**
          * Retrieves a bank manager by their ID.
          *
          * @param managerId the ID of the manager to retrieve
          * @return the bank manager with the specified ID
          * @throws BankManagerRepositoryException if an error occurs while retrieving the manager
          */
         BankManager getManagerById(String managerId) throws BankManagerRepositoryException;

         /**
          * Retrieves all bank managers.
          *
          * @return a list of all bank managers
          * @throws BankManagerRepositoryException if an error occurs while retrieving the managers
          */
         ManagerList getAllManagers() throws BankManagerRepositoryException;

         /**
          * Inserts a new bank manager.
          *
          * @param manager the bank manager to insert
          * @throws BankManagerRepositoryException if an error occurs while inserting the manager
          */
         void insertManager(BankManager manager) throws BankManagerRepositoryException;

         /**
          * Updates an existing bank manager.
          *
          * @param manager the bank manager to update
          * @throws BankManagerRepositoryException if an error occurs while updating the manager
          */
         void updateManager(BankManager manager) throws BankManagerRepositoryException;

         /**
          * Deletes a bank manager by their ID.
          *
          * @param managerId the ID of the manager to delete
          * @throws BankManagerRepositoryException if an error occurs while deleting the manager
          */
         void deleteManager(String managerId) throws BankManagerRepositoryException;
     }