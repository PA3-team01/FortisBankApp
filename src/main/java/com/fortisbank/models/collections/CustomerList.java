package com.fortisbank.models.collections;

import com.fortisbank.models.users.Customer;
import com.fortisbank.utils.CustomerComparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Liste pour la gestion des clients
 */
public class CustomerList extends ArrayList<Customer> {

    // ------------------- Constructors -------------------

    public CustomerList() {
        super();
    }

    public CustomerList(Iterable<Customer> customers) {
        customers.forEach(this::add);
    }

    // ------------------- Sorting Methods -------------------

    /**
     * Trie les clients par nom
     */
    public void sortByName() {
        this.sort(CustomerComparators.BY_NAME);
    }

    /**
     * Trie les clients par solde decroissant
     */
    public void sortByBalanceDescending() {
        this.sort(CustomerComparators.BY_BALANCE.reversed());
    }

    /**
     * Trie les clients par nombre de transaction
     */
    public void sortByTransactionCountAscending() {
        this.sort(CustomerComparators.BY_TRANSACTION_COUNT);
    }

    /**
     * Trie les clients par nombre de transaction decroissant
     */
    public void sortByTransactionCountDescending() {
        this.sort(CustomerComparators.BY_TRANSACTION_COUNT.reversed());
    }

    // ------------------- Filtering Methods -------------------

    /**
     * Filtre les client par solde minimum
     * @param minBalance Solde minimum
     * @return Customer List filtrer
     */
    public CustomerList filterByMinBalance(double minBalance) {
        return this.stream()
                .filter(c -> c.getBalance().compareTo(BigDecimal.valueOf(minBalance)) >= 0)
                .collect(Collectors.toCollection(CustomerList::new));
    }

    /**
     * Filtre les clients qui contient x
     * @param substring x a rechercher
     * @return Customer List filtrer
     */
    public CustomerList filterByNameContains(String substring) {
        return this.stream()
                .filter(c -> c.getFullName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toCollection(CustomerList::new));
    }

    // ------------------- String Representation -------------------

    @Override
    public String toString() {
        return "CustomerList{" + super.toString() + "}";
    }
}
