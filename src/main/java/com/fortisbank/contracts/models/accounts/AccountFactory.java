package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.models.users.Customer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Factory class for creating different types of accounts.
 */
public class AccountFactory {

    /**
     * Creates an account of the specified type.
     *
     * @param type the type of the account to create
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param openedDate the date when the account was opened
     * @param initialBalance the initial balance of the account
     * @param extraParams additional parameters required for certain account types
     * @return the created account
     * @throws IllegalArgumentException if the account type is unsupported
     */
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