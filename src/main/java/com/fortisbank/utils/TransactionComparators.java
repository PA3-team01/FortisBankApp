package com.fortisbank.utils;

import com.fortisbank.models.transactions.Transaction;

import java.util.Comparator;

/**
 * Utility class providing comparators for Transaction objects.
 */
public class TransactionComparators {

    /**
     * Comparator for comparing transactions by their date.
     */
    public static final Comparator<Transaction> BY_DATE =
            Comparator.comparing(Transaction::getTransactionDate);

    /**
     * Comparator for comparing transactions by their amount.
     */
    public static final Comparator<Transaction> BY_AMOUNT =
            Comparator.comparing(Transaction::getAmount);

    /**
     * Comparator for comparing transactions by their type.
     */
    public static final Comparator<Transaction> BY_TYPE =
            Comparator.comparing(Transaction::getTransactionType);
}