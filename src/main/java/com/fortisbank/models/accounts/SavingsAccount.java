package com.fortisbank.models.accounts;
import com.fortisbank.models.Customer;
import java.math.BigDecimal;
import java.util.Date;

public class SavingsAccount extends Account {
    private BigDecimal annualInterestRate;

    public SavingsAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, BigDecimal interestRate) {
        super(accountNumber, customer, AccountType.SAVINGS, openedDate, initialBalance);
        this.annualInterestRate = interestRate;
    }

    public void applyAnnualInterest() {
        BigDecimal interest = availableBalance.multiply(annualInterestRate);
        deposit(interest);
    }
}
