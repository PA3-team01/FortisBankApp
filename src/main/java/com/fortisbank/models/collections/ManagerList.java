package com.fortisbank.models.collections;

import com.fortisbank.models.users.BankManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A typed list for managing BankManager objects.
 * Provides filtering and extensibility for manager-specific operations.
 */
public class ManagerList extends ArrayList<BankManager> {

    // ------------------- Constructors -------------------

    public ManagerList() {
        super();
    }

    public ManagerList(Iterable<BankManager> managers) {
        managers.forEach(this::add);
    }

    // ------------------- Filtering -------------------

    /**
     *  Filtre les Manager qui contient x
     * @param substring x a rechercher
     * @return Manager List filtrer
     */
    public ManagerList filterByNameContains(String substring) {
        return this.stream()
                .filter(m -> m.getFullName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toCollection(ManagerList::new));
    }

    // ------------------- String Representation -------------------

    @Override
    public String toString() {
        return "ManagerList{" + super.toString() + "}";
    }
}
