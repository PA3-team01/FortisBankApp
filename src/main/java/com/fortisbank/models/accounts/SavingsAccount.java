package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Date;

public class SavingsAccount extends Account implements InterestBearingAccount {

    private BigDecimal annualInterestRate;
    private LocalDate lastInterestApplied;

    public SavingsAccount(String accountNumber, Customer customer, Date openedDate,
                          BigDecimal initialBalance, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.SAVINGS, openedDate, initialBalance);
        this.annualInterestRate = interestRate;
        this.lastInterestApplied = null;
    }

    // -------------------------------------------------------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------------------------------------------------------

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    public BigDecimal getInterestRate() {
        return getAnnualInterestRate();
    }

    public LocalDate getLastInterestApplied() {
        return lastInterestApplied;
    }

    public void setLastInterestApplied(LocalDate lastInterestApplied) {
        this.lastInterestApplied = lastInterestApplied;
    }

    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    // -------------------------------------------------------------------------------------
    // INTEREST CALCULATION ELIGIBILITY
    // -------------------------------------------------------------------------------------

    /**
     * Annual interest should only be applied once per calendar year.
     */
    public boolean isEligibleForInterestCalculation() {
        if (lastInterestApplied == null) return true;
        LocalDate now = LocalDate.now();
        return lastInterestApplied.getYear() < now.getYear();
    }

    // -------------------------------------------------------------------------------------
    // DISPLAY INFO
    // -------------------------------------------------------------------------------------

    @Override
    public String displayAccountInfo() {
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$\n" +
                "Annual Interest Rate: " + df.format(annualInterestRate.multiply(BigDecimal.valueOf(100))) + "%\n" +
                "Last Interest Applied: " + (lastInterestApplied != null ? lastInterestApplied : "Never") + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}
