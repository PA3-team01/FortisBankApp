package com.fortisbank.data.repositories;

import com.fortisbank.models.collections.ManagerList;
import com.fortisbank.models.users.BankManager;

public interface IBankManagerRepository {

    BankManager getManagerById(String managerId);

    ManagerList getAllManagers();

    void insertManager(BankManager manager);

    void updateManager(BankManager manager);

    void deleteManager(String managerId);
}
