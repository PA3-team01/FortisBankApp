package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Date;

/**
 * Class representing a savings account.
 * Extends the Account class and implements the InterestBearingAccount interface.
 */
public class SavingsAccount extends Account implements InterestBearingAccount {

    /**
     * The annual interest rate applied to the account.
     */
    private BigDecimal annualInterestRate;

    /**
     * The date when interest was last applied.
     */
    private LocalDate lastInterestApplied;

    /**
     * Constructor initializing a savings account with specified values.
     *
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param openedDate the date when the account was opened
     * @param initialBalance the initial balance of the account
     * @param interestRate the annual interest rate applied to the account
     */
    public SavingsAccount(String accountNumber, Customer customer, Date openedDate,
                          BigDecimal initialBalance, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.SAVINGS, openedDate, initialBalance);
        this.annualInterestRate = interestRate;
        this.lastInterestApplied = null;
    }

    // -------------------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------------------

    /**
     * Returns the annual interest rate applied to the account.
     *
     * @return the annual interest rate
     */
    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    /**
     * Sets the annual interest rate applied to the account.
     *
     * @param annualInterestRate the annual interest rate to set
     */
    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    /**
     * Returns the interest rate applied to the account.
     *
     * @return the interest rate
     */
    @Override
    public BigDecimal getInterestRate() {
        return getAnnualInterestRate();
    }

    /**
     * Returns the date when interest was last applied.
     *
     * @return the last interest applied date
     */
    public LocalDate getLastInterestApplied() {
        return lastInterestApplied;
    }

    /**
     * Sets the date when interest was last applied.
     *
     * @param lastInterestApplied the date to set
     */
    public void setLastInterestApplied(LocalDate lastInterestApplied) {
        this.lastInterestApplied = lastInterestApplied;
    }

    /**
     * Returns the credit limit of the account.
     * Savings accounts do not have a credit limit, so this method returns null.
     *
     * @return null
     */
    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    // -------------------------------------------------------------------------------------
    // INTEREST CALCULATION ELIGIBILITY
    // -------------------------------------------------------------------------------------

    /**
     * Checks if the account is eligible for interest calculation.
     * Annual interest should only be applied once per calendar year.
     *
     * @return true if eligible for interest calculation, false otherwise
     */
    public boolean isEligibleForInterestCalculation() {
        if (lastInterestApplied == null) return true;
        return lastInterestApplied.getYear() < LocalDate.now().getYear();
    }

    // -------------------------------------------------------------------------------------
    // DISPLAY INFO
    // -------------------------------------------------------------------------------------

    /**
     * Displays account information in a formatted string.
     *
     * @return a string containing account information
     */
    @Override
    public String displayAccountInfo() {
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$\n" +
                "Annual Interest Rate: " + df.format(getAnnualInterestRate().multiply(BigDecimal.valueOf(100))) + "%\n" +
                "Last Interest Applied: " + (lastInterestApplied != null ? lastInterestApplied : "Never") + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}