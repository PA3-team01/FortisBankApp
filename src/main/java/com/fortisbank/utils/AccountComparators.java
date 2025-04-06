package com.fortisbank.utils;

import com.fortisbank.models.accounts.Account;

import java.util.Comparator;

/**
 * Utility class providing comparators for Account objects.
 */
public class AccountComparators {

    /**
     * Comparator for comparing accounts by their available balance.
     */
    public static final Comparator<Account> BY_BALANCE =
            Comparator.comparing(Account::getAvailableBalance);

    /**
     * Comparator for comparing accounts by their type.
     */
    public static final Comparator<Account> BY_TYPE =
            Comparator.comparing(Account::getAccountType);

    /**
     * Comparator for comparing accounts by their creation date.
     */
    public static final Comparator<Account> BY_CREATED_DATE =
            Comparator.comparing(Account::getOpenedDate);
}