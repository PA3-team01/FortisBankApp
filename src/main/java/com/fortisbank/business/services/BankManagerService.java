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

public class BankManagerService implements IBankManagerService {

    private static final Map<StorageMode, BankManagerService> instances = new EnumMap<>(StorageMode.class);

    private final IBankManagerRepository managerRepository;

    private BankManagerService(StorageMode storageMode) {
        this.managerRepository = RepositoryFactory.getInstance(storageMode).getBankManagerRepository();
    }

    public static synchronized BankManagerService getInstance(StorageMode storageMode) {
        return instances.computeIfAbsent(storageMode, BankManagerService::new);
    }

    // ------------------- Core Business Methods -------------------

    @Override
    public Customer createCustomer(String firstName, String lastName, String email, String phone, String hashedPassword, String pinHash) {
        // Implementation depends on whether manager creates users directly or uses RegisterService
        return null;
    }

    @Override
    public boolean approveAccount(Account account) {
        account.setActive(true);
        return true;
    }

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

    @Override
    public boolean deleteCustomer(Customer customer) {
        // You can delegate to CustomerService or remove via repository directly
        return false;
    }

    @Override
    public void generateCustomerReport() {
        // Placeholder — would generate a report using available data (e.g., PDF, export)
    }

    // ------------------- Login Support -------------------

    public ManagerList getAllManagers() {
        return managerRepository.getAllManagers();
    }

    public BankManager getManagerByEmail(String email) {
        for (BankManager manager : getAllManagers()) {
            if (manager.getEmail().equalsIgnoreCase(email)) {
                return manager;
            }
        }
        return null;
    }
}
