package com.fortisbank.utils;

import com.fortisbank.models.Customer;

import java.util.Comparator;

public class CustomerComparators {

    public static final Comparator<Customer> BY_NAME =
            Comparator.comparing(Customer::getFullName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Customer> BY_BALANCE =
            Comparator.comparing(Customer::getBalance);


    public static final Comparator<Customer> BY_NAME_DESCENDING =
            BY_NAME.reversed();

    public static final Comparator<Customer> BY_BALANCE_DESCENDING =
            BY_BALANCE.reversed();

}