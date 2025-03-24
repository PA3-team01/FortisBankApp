package com.fortisbank.utils;

import com.fortisbank.models.accounts.Account;
import java.math.BigDecimal;
import java.util.Comparator;


public class AccountComparators {
    public static final Comparator<Account> BY_BALANCE =
            Comparator.comparing(Account::getAvailableBalance);

    public static final Comparator<Account> BY_TYPE =
            Comparator.comparing(Account::getAccountType);

    public static final Comparator<Account> BY_CREATED_DATE =
            Comparator.comparing(Account::getOpenedDate);
}
