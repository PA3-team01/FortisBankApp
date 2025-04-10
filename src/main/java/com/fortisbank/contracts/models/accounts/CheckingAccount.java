package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.utils.IdGenerator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

/**
 * Class representing a checking account.
 * Extends the Account class.
 */
public class CheckingAccount extends Account {
    /**
     * The limit of free transactions.
     */
    public static final int FREE_TRANSACTION_LIMIT = 2;

    /**
     * The fee for transactions exceeding the free limit.
     */
    public static final BigDecimal TRANSACTION_FEE = new BigDecimal("5.00");

    /**
     * Constructor initializing a checking account with a customer and initial balance.
     *
     * @param customer the customer who owns the account
     * @param initialBalance the initial balance of the account
     */
    public CheckingAccount(Customer customer, BigDecimal initialBalance) {
        super(IdGenerator.generateId(), customer, AccountType.CHECKING, new Date(), initialBalance);
    }

    /**
     * Constructor initializing a checking account with specified values.
     *
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param openedDate the date when the account was opened
     * @param initialBalance the initial balance of the account
     */
    public CheckingAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance) {
        super(accountNumber, customer, AccountType.CHECKING, openedDate, initialBalance);
    }

    /**
     * Returns the credit limit of the account.
     * Checking accounts do not have a credit limit, so this method returns null.
     *
     * @return null
     */
    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    /**
     * Displays account information in a formatted string.
     *
     * @return a string containing account information
     */
    @Override
    public String displayAccountInfo(){
        // Format the balance to two decimal places
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$" + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}