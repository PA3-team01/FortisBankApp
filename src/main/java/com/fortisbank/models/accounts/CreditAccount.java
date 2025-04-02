package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    @Override
    public String displayAccountInfo() {
        //pour .00 apres l'argent
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$" + "\n" +
                "Credit Limit: " + getCreditLimit() + "\n" +
                "Interest Rate: " + getInterestRate() + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}
