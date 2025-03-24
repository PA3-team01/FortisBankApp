package com.fortisbank.models.collections;

import com.fortisbank.models.accounts.Account;

import java.util.ArrayList;
import java.util.stream.Collector;

/**
 * A specialized collection class that extends ArrayList to manage a list of Account objects.
 * Provides utility methods and a custom collector for working with Account objects.
 *
 * AccountList allows you to initialize and manipulate collections of Account instances
 * and serves as a utility class to extend the functionality of ArrayList specific to Account objects.
 */
public class AccountList extends ArrayList<Account> {

    public AccountList() {
        super();
    }

    public AccountList(Iterable<Account> accounts) {
        accounts.forEach(this::add);
    }




    // Future methods: filterByType, getTotalBalance, etc.
}
