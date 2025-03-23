package com.fortisbank.models.collections;

import com.fortisbank.models.transactions.Transaction;

import java.util.ArrayList;

public class TransactionList extends ArrayList<Transaction> {

    public TransactionList() {
        super();
    }

    public TransactionList(Iterable<Transaction> transactions) {
        transactions.forEach(this::add);
    }

    // Future methods: filterByDate, getTotalAmount, groupByAccount, etc.
}
