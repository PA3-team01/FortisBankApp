package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.util.Date;

public class SavingsAccount extends Account {
    private final BigDecimal annualInterestRate;

    public SavingsAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.SAVINGS, openedDate, initialBalance);
        this.annualInterestRate = interestRate;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }



    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    @Override
    public String displayAccountInfo() {
        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + getAvailableBalance() + "\n" +
                "Annual Interest Rate: " + getAnnualInterestRate() + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}
