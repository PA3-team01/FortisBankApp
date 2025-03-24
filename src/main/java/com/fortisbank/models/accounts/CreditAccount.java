package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;

import java.math.BigDecimal;
import java.util.Date;

public class CreditAccount extends Account {
    private BigDecimal creditLimit;
    private BigDecimal interestRate; // TODO: Implement interest applying logic in business logic layer.

    public CreditAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal creditLimit, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.CREDIT, openedDate, BigDecimal.ZERO);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (availableBalance.add(creditLimit).compareTo(amount) < 0) {
            throw new IllegalArgumentException("Exceeds credit limit.");
        }
        super.withdraw(amount);
    }

    public void applyInterest() {
        BigDecimal interest = availableBalance.multiply(interestRate);
        applyFees(interest, "Credit account interest applied.");
    }

    @Override
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
}
