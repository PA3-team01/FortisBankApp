package com.fortisbank.models.accounts;

import com.fortisbank.models.Customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class CurrencyAccount extends Account {
    private String currencyCode;
    private Date lastActiveDate;

    public CurrencyAccount(String accountNumber, Customer customer, Date openedDate, BigDecimal initialBalance, String currencyCode) {
        super(accountNumber, customer, AccountType.CURRENCY, openedDate, initialBalance);
        this.currencyCode = currencyCode.toUpperCase();
        this.lastActiveDate = new Date();
    }

    @Override
    public void deposit(BigDecimal amount) {
        super.deposit(amount);
        lastActiveDate = new Date();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        super.withdraw(amount);
        lastActiveDate = new Date();
    }

    @Override
    public BigDecimal getCreditLimit() {
        return null;
    }

    public void depositInDifferentCurrency(BigDecimal amount, String fromCurrency) {
        BigDecimal exchangeRate = CurrencyType.getInstance().getExchangeRate(fromCurrency);
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid currency: " + fromCurrency);
        }
        BigDecimal convertedAmount = amount.multiply(exchangeRate);
        super.deposit(convertedAmount);
        lastActiveDate = new Date();
    }

    public void withdrawInDifferentCurrency(BigDecimal amount, String toCurrency) {
        BigDecimal exchangeRate = CurrencyType.getInstance().getExchangeRate(toCurrency);
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid currency: " + toCurrency);
        }
        BigDecimal convertedAmount = amount.divide(exchangeRate, 4, RoundingMode.HALF_UP);
        super.withdraw(convertedAmount);
        lastActiveDate = new Date();
    }

    public boolean checkInactiveForOneYear() {
        return (new Date().getTime() - lastActiveDate.getTime()) >= 365L * 24 * 60 * 60 * 1000;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String newCurrencyCode) {
        this.currencyCode = newCurrencyCode.toUpperCase();
    }
}
