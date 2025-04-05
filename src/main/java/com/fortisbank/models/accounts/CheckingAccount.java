package com.fortisbank.models.accounts;

import com.fortisbank.models.users.Customer;
import com.fortisbank.utils.IdGenerator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

public class CheckingAccount extends Account {
    public static final int FREE_TRANSACTION_LIMIT = 2;
    public static final BigDecimal TRANSACTION_FEE = new BigDecimal("5.00");

    public CheckingAccount(Customer customer, BigDecimal initialBalance) {
        super(IdGenerator.generateId(), customer, AccountType.CHECKING, new Date(), initialBalance);
    }

    public CheckingAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance) {
        super(accountNumber, customer, AccountType.CHECKING, openedDate, initialBalance);
    }

    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    @Override
    public String displayAccountInfo(){
        //pour .00 apres l'argent
        DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance());

        return "Account Number: " + getAccountNumber() + "\n" +
                "Account Type: " + getAccountType() + "\n" +
                "Opened Date: " + getOpenedDate() + "\n" +
                "Available Balance: " + df.format(getAvailableBalance()) + "$" + "\n" +
                "Customer Name: " + getCustomer().getFullName();
    }
}
