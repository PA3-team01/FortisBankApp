package com.fortisbank.contracts.models.accounts;

import com.fortisbank.contracts.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Date;

/**
 * Class representing a credit account.
 * Extends the Account class and implements the InterestBearingAccount interface.
 */
public class CreditAccount extends Account implements InterestBearingAccount {

    /**
     * The credit limit of the account.
     */
    private BigDecimal creditLimit;

    /**
     * The interest rate applied to the account.
     */
    private BigDecimal interestRate;

    /**
     * The date when interest was last applied.
     */
    private LocalDate lastInterestApplied;

    /**
     * Constructor initializing a credit account with specified values.
     *
     * @param accountNumber the account number
     * @param customer the customer who owns the account
     * @param openedDate the date when the account was opened
     * @param creditLimit the credit limit of the account
     * @param interestRate the interest rate applied to the account
     */
    public CreditAccount(String accountNumber, Customer customer, Date openedDate,
                         BigDecimal creditLimit, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.CREDIT, openedDate, BigDecimal.ZERO);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.lastInterestApplied = null;
    }

    // -------------------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------------------

    /**
     * Returns the credit limit of the account.
     *
     * @return the credit limit
     */
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the credit limit of the account.
     *
     * @param creditLimit the credit limit to set
     */
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    /**
     * Returns the interest rate applied to the account.
     *
     * @return the interest rate
     */
    @Override
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the interest rate applied to the account.
     *
     * @param interestRate the interest rate to set
     */
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
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

    // -------------------------------------------------------------------------------------
    // INTEREST CALCULATION ELIGIBILITY
    // -------------------------------------------------------------------------------------

    /**
     * Checks if the account is eligible for interest calculation.
     * Monthly interest should only be applied once per calendar month.
     *
     * @return true if eligible for interest calculation, false otherwise
     */
    public boolean isEligibleForInterestCalculation() {
        if (lastInterestApplied == null) return true;
        LocalDate now = LocalDate.now();
        return lastInterestApplied.getYear() < now.getYear()
                || lastInterestApplied.getMonthValue() < now.getMonthValue();
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
                "Credit Limit: " + df.format(getCreditLimit()) + "$\n" +
                "Interest Rate: " + df.format(getInterestRate().multiply(BigDecimal.valueOf(100))) + "%\n" +
                "Last Interest Applied: " + (lastInterestApplied != null ? lastInterestApplied : "Never") + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}