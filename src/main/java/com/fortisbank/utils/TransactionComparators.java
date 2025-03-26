package com.fortisbank.utils;

import com.fortisbank.models.transactions.Transaction;

import java.util.Comparator;

public class TransactionComparators {
    public static final Comparator<Transaction> BY_DATE =
            Comparator.comparing(Transaction::getTransactionDate);

    public static final Comparator<Transaction> BY_AMOUNT =
            Comparator.comparing(Transaction::getAmount);

    public static final Comparator<Transaction> BY_TYPE =
            Comparator.comparing(Transaction::getTransactionType);
}