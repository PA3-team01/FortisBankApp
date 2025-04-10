package com.fortisbank.contracts.utils;

import com.fortisbank.contracts.models.users.Customer;

import java.util.Comparator;

/**
 * Utility class providing comparators for Customer objects.
 */
public class CustomerComparators {

    /**
     * Comparator for comparing customers by their full name in a case-insensitive order.
     */
    public static final Comparator<Customer> BY_NAME =
            Comparator.comparing(Customer::getFullName, String.CASE_INSENSITIVE_ORDER);

    /**
     * Comparator for comparing customers by their balance.
     */
    public static final Comparator<Customer> BY_BALANCE =
            Comparator.comparing(Customer::getBalance);

    /**
     * Comparator for comparing customers by their full name in descending order.
     */
    public static final Comparator<Customer> BY_NAME_DESCENDING =
            BY_NAME.reversed();

    /**
     * Comparator for comparing customers by their balance in descending order.
     */
    public static final Comparator<Customer> BY_BALANCE_DESCENDING =
            BY_BALANCE.reversed();

    /**
     * Comparator for comparing customers by their transaction count.
     */
    public static final Comparator<Customer> BY_TRANSACTION_COUNT =
            Comparator.comparingInt(c -> c.getTransactions().size());

    /**
     * Comparator for comparing customers by their transaction count in descending order.
     */
    public static final Comparator<Customer> BY_TRANSACTION_COUNT_DESCENDING =
            BY_TRANSACTION_COUNT.reversed();

    /**
     * Comparator for comparing customers by their full name and then by their balance.
     */
    public static final Comparator<Customer> BY_NAME_AND_BALANCE =
            BY_NAME.thenComparing(BY_BALANCE);

    /**
     * Comparator for comparing customers by their full name in descending order and then by their balance in descending order.
     */
    public static final Comparator<Customer> BY_NAME_AND_BALANCE_DESCENDING =
            BY_NAME_DESCENDING.thenComparing(BY_BALANCE_DESCENDING);
}