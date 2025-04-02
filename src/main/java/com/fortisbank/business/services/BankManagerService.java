package com.fortisbank.business.services;

import com.fortisbank.data.repositories.IBankManagerRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.ManagerList;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service de gestion des manager
 */
public class BankManagerService implements IBankManagerService {

    private static final Map<StorageMode, BankManagerService> instances = new EnumMap<>(StorageMode.class);

    private final IBankManagerRepository managerRepository;

    private BankManagerService(StorageMode storageMode) {
        this.managerRepository = RepositoryFactory.getInstance(storageMode).getBankManagerRepository();
    }

    public static synchronized BankManagerService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, BankManagerService::new);
    }

    // ------------------- CRUD OPERATIONS -------------------

    // Create

    /**
     * Cree un nouveau manager
     * @param manager manager
     * @return le manager cree
     */
    public BankManager createManager(BankManager manager) { // ** Do not call directly, use register service **
        managerRepository.insertManager(manager);
        return manager;
    }

    // ------------------- Core Business Methods -------------------

    @Override
    public Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash) {
        // Implementation depends on whether manager creates users directly or uses RegisterService
        //TODO: Implement this method
        return null;
    }

    /**
     * Approbation d'un compte
     * @param account Compte
     * @return true si approuver
     */
    @Override
    public boolean approveAccount(Account account) {
        account.setActive(true);
        return true;
    }

    /**
     * Fermer un compte
     * @param account Compte
     * @return true si la fermeture du compte reussit
     */
    @Override
    public boolean closeAccount(Account account) {
        try {
            account.closeAccount();
            return true;
        } catch (IllegalStateException e) {
            System.err.println("Failed to close account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Suppression d'un client
     * @param customer Client
     * @return
     */
    @Override
    public boolean deleteCustomer(Customer customer) {
        // You can delegate to CustomerService or remove via repository directly
        return false;
    }

    /**
     * Generer un rapport client
     */
    @Override
    public void generateCustomerReport() {
        // Placeholder — would generate a report using available data (e.g., PDF, export)
    }

    // ------------------- Login Support -------------------

    /**
     * Recupere tout les managers
     * @return la liste des managers
     */
    public ManagerList getAllManagers() {
        return managerRepository.getAllManagers();
    }

    /**
     * Recherche un manager par email
     * @param email Email
     * @return le manager trouver ou null
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
     * Verification pour voir si un email est deja utilise
     * @param email Email
     * @return si l'email existe deja
     */
    public boolean emailExists(String email) {
        return getAllManagers().stream().anyMatch(manager -> manager.getEmail().equalsIgnoreCase(email));
    }
}
