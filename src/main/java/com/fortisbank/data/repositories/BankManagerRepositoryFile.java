package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.collections.ManagerList;
import com.fortisbank.models.users.BankManager;

import java.io.File;
import java.util.List;

/**
 * Repository class for managing bank manager data stored in a file.
 * Extends the FileRepository class and implements the IBankManagerRepository interface.
 */
public class BankManagerRepositoryFile extends FileRepository<BankManager> implements IBankManagerRepository {

    private static final File file = new File("data/managers.ser"); // File to store manager data
    private static BankManagerRepositoryFile instance; // Singleton instance

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the repository with the specified file.
     */
    private BankManagerRepositoryFile() {
        super(file);
    }

    /**
     * Returns the singleton instance of BankManagerRepositoryFile.
     * Synchronized to prevent multiple threads from creating multiple instances.
     *
     * @return the singleton instance of BankManagerRepositoryFile
     */
    public static synchronized BankManagerRepositoryFile getInstance() {
        if (instance == null) {
            instance = new BankManagerRepositoryFile();
        }
        return instance;
    }

    /**
     * Retrieves a bank manager by their ID.
     *
     * @param id the ID of the manager to retrieve
     * @return the bank manager with the specified ID, or null if not found
     */
    @Override
    public BankManager getManagerById(String id) {
        return readAll().stream()
                .filter(m -> m.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserts a new bank manager into the file.
     *
     * @param manager the bank manager to insert
     */
    @Override
    public void insertManager(BankManager manager) {
        List<BankManager> managers = readAll();
        managers.add(manager);
        writeAll(managers);
    }

    /**
     * Updates an existing bank manager in the file.
     *
     * @param manager the bank manager to update
     */
    @Override
    public void updateManager(BankManager manager) {
        List<BankManager> managers = readAll();
        for (int i = 0; i < managers.size(); i++) {
            if (managers.get(i).getUserId().equals(manager.getUserId())) {
                managers.set(i, manager);
                break;
            }
        }
        writeAll(managers);
    }

    /**
     * Deletes a bank manager from the file.
     *
     * @param id the ID of the manager to delete
     */
    @Override
    public void deleteManager(String id) {
        List<BankManager> managers = readAll();
        managers.removeIf(m -> m.getUserId().equals(id));
        writeAll(managers);
    }

    /**
     * Retrieves all bank managers from the file.
     *
     * @return a list of all bank managers
     */
    @Override
    public ManagerList getAllManagers() {
        List<BankManager> list = readAll();
        System.out.println("🔍 [getAllManagers] Loaded managers: " + list.size());
        for (BankManager m : list) {
            System.out.println(" → Email: " + m.getEmail());
            System.out.println("   Name: " + m.getFirstName() + " " + m.getLastName());
            System.out.println("   User ID: " + m.getUserId());
        }

        return new ManagerList(list);
    }
}