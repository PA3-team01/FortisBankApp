package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.util.Date;

public class AccountFactory {
    public static Account createAccount(AccountType type, String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, Object... extraParams) {
        return switch (type) {
            case CHECKING -> new CheckingAccount(accountNumber, customer, openedDate, initialBalance);
            case SAVINGS ->
                    new SavingsAccount(accountNumber, customer, openedDate, initialBalance, (BigDecimal) extraParams[0]);
            case CREDIT ->
                    new CreditAccount(accountNumber, customer, openedDate, (BigDecimal) extraParams[0], (BigDecimal) extraParams[1]);
            case CURRENCY ->
                    new CurrencyAccount(accountNumber, customer, openedDate, initialBalance, (String) extraParams[0]);
            default -> throw new IllegalArgumentException("Unsupported account type.");
        };
    }
}
