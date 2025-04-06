package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.ManagerList;
import com.fortisbank.models.users.BankManager;

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
     */
    BankManager getManagerById(String managerId);

    /**
     * Retrieves all bank managers.
     *
     * @return a list of all bank managers
     */
    ManagerList getAllManagers();

    /**
     * Inserts a new bank manager.
     *
     * @param manager the bank manager to insert
     */
    void insertManager(BankManager manager);

    /**
     * Updates an existing bank manager.
     *
     * @param manager the bank manager to update
     */
    void updateManager(BankManager manager);

    /**
     * Deletes a bank manager by their ID.
     *
     * @param managerId the ID of the manager to delete
     */
    void deleteManager(String managerId);
}