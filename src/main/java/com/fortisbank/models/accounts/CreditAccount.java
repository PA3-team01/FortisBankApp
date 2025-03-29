package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.util.Date;

public class CreditAccount extends Account {
    private final BigDecimal creditLimit;
    private final BigDecimal interestRate;

    public CreditAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal creditLimit, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.CREDIT, openedDate, BigDecimal.ZERO);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
}
