package com.fortisbank.models.collections;

import com.fortisbank.models.Customer;
import com.fortisbank.utils.CustomerComparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CustomerList extends ArrayList<Customer> {

    public CustomerList() {
        super();
    }

    public CustomerList(Iterable<Customer> customers) {
        customers.forEach(this::add);
    }

    // Sorting methods
    public void sortByName() {
        this.sort(CustomerComparators.BY_NAME);
    }

    public void sortByBalanceDescending() {
        this.sort(CustomerComparators.BY_BALANCE.reversed());
    }

    public void sortByTransactionCountAscending() {
        this.sort(CustomerComparators.BY_TRANSACTION_COUNT);
    }

    public void sortByTransactionCountDescending() {
        this.sort(CustomerComparators.BY_TRANSACTION_COUNT.reversed());
    }

    // Filtering methods
    public CustomerList filterByMinBalance(double minBalance) {
        return this.stream()
                .filter(c -> c.getBalance().compareTo(BigDecimal.valueOf(minBalance)) >= 0)
                .collect(Collectors.toCollection(CustomerList::new));
    }

    public CustomerList filterByNameContains(String substring) {
        return this.stream()
                .filter(c -> c.getFullName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toCollection(CustomerList::new));
    }

    @Override
    public String toString() {
        return "CustomerList{" + super.toString() + "}";
    }
}
