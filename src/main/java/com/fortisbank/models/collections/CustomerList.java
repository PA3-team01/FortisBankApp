package com.fortisbank.models.collections;

import com.fortisbank.models.Customer;

import java.util.ArrayList;

public class CustomerList extends ArrayList<Customer> {

    public CustomerList() {
        super();
    }

    public CustomerList(Iterable<Customer> customers) {
        customers.forEach(this::add);
    }

    // Add future methods like filterByBalance, sortByName, etc.
}
