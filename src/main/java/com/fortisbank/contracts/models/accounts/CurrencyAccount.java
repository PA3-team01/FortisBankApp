package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

/**
 * Class representing a currency account.
 * Extends the Account class.
 */
public class CurrencyAccount extends Account {
    /**
     * The currency code of the account.
     */
    private String currencyCode;

    /**
     * The date when the account was last active.
     */
    private Date lastActiveDate;

    /**
     * Constructor initializing a currency account with specified values.
     *
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param openedDate the date when the account was opened
     * @param initialBalance the initial balance of the account
     * @param currencyCode the currency code of the account
     */
    public CurrencyAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, String currencyCode) {
        super(accountNumber, customer, AccountType.CURRENCY, openedDate, initialBalance);
        this.currencyCode = currencyCode.toUpperCase();
        this.lastActiveDate = new Date();
    }

    /**
     * Returns the credit limit of the account.
     * Currency accounts do not have a credit limit, so this method returns null.
     *
     * @return null
     */
    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    /**
     * Returns the currency code of the account.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code of the account.
     *
     * @param newCurrencyCode the new currency code to set
     */
    public void setCurrencyCode(String newCurrencyCode) {
        this.currencyCode = newCurrencyCode.toUpperCase();
    }

    /**
     * Returns the date when the account was last active.
     *
     * @return the last active date
     */
    public Date getLastActiveDate() {
        return lastActiveDate;
    }

    /**
     * Updates the last active date to the current date.
     */
    public void updateLastActiveDate() {
        this.lastActiveDate = new Date();
    }

    /**
     * Displays account information in a formatted string.
     *
     * @return a string containing account information
     */
    @Override
    public String displayAccountInfo() {
        // Format the balance to two decimal places
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$" + "\n" +
                "Currency Code: " + getCurrencyCode() + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}