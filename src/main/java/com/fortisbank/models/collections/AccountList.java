package com.fortisbank.models.collections;

import com.fortisbank.models.accounts.Account;

import java.util.ArrayList;

public class AccountList extends ArrayList<Account> {

    public AccountList() {
        super();
    }

    public AccountList(Iterable<Account> accounts) {
        accounts.forEach(this::add);
    }

    // Future methods: filterByType, getTotalBalance, etc.
}
