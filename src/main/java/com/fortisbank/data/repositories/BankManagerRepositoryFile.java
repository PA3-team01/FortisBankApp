package com.fortisbank.data.repositories;

import com.fortisbank.data.file.FileRepository;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.collections.ManagerList;

import java.io.File;
import java.util.List;

public class BankManagerRepositoryFile extends FileRepository<BankManager> implements IBankManagerRepository {

    private static final File file = new File("data/managers.ser");
    private static BankManagerRepositoryFile instance;

    private BankManagerRepositoryFile() {
        super(file);
    }

    public static synchronized BankManagerRepositoryFile getInstance() {
        if (instance == null) {
            instance = new BankManagerRepositoryFile();
        }
        return instance;
    }

    @Override
    public BankManager getManagerById(String id) {
        return readAll().stream()
                .filter(m -> m.getUserId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertManager(BankManager manager) {
        List<BankManager> managers = readAll();
        managers.add(manager);
        writeAll(managers);
    }

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

    @Override
    public void deleteManager(String id) {
        List<BankManager> managers = readAll();
        managers.removeIf(m -> m.getUserId().equals(id));
        writeAll(managers);
    }

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
