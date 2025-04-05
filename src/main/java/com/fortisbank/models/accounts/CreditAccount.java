package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Date;

public class CreditAccount extends Account implements InterestBearingAccount {

    private BigDecimal creditLimit;
    private BigDecimal interestRate;
    private LocalDate lastInterestApplied;

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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getLastInterestApplied() {
        return lastInterestApplied;
    }

    public void setLastInterestApplied(LocalDate lastInterestApplied) {
        this.lastInterestApplied = lastInterestApplied;
    }

    // -------------------------------------------------------------------------------------
    // INTEREST CALCULATION ELIGIBILITY
    // -------------------------------------------------------------------------------------

    /**
     * Monthly interest should only be applied once per calendar month.
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
