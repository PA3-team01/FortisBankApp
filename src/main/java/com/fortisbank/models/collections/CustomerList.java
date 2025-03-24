package com.fortisbank.models.collections;

import com.fortisbank.models.Customer;
import com.fortisbank.utils.CustomerComparators;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CustomerList extends ArrayList<Customer> {

    //Sort list name ascending order
    public void sortByName() {
        this.sort(CustomerComparators.BY_NAME);
    }

    // Sort list balance descending order
    public void sortByBalanceDescending() {
        this.sort(CustomerComparators.BY_BALANCE.reversed());
    }

    //Sort list transaction ascending order
    public void sortByTransactionCount() {
        this.sort(CustomerComparators.BY_TRANSACTION_COUNT);
    }

    //Filter list by minimum balance
    public CustomerList filterByMinBalance(double minBalance) {
        return this.stream()
                .filter(c -> c.getBalance().compareTo(BigDecimal.valueOf(minBalance)) >= 0)
                .collect(Collectors.toCollection(CustomerList::new));
    }

    //Filter list by name containing a substring (case-insensitive)
    public CustomerList filterByNameContains(String substring) {
        return this.stream()
                .filter(c -> c.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toCollection(CustomerList::new));
    }

    @Override
    public String toString() {
        return "CustomerList{" + super.toString() + "}";
    }
}
