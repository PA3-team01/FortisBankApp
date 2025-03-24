package com.fortisbank.models.reports;

import com.fortisbank.models.Customer;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CustomerStatementReport extends Report {

    private final Customer customer;
    private final List<Transaction> transactions;
    private final BigDecimal openingBalance;
    private final BigDecimal closingBalance;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;

    public CustomerStatementReport(
            Customer customer,
            List<Transaction> transactions,
            BigDecimal openingBalance,
            BigDecimal closingBalance,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        super(CustomerStatementReport.class.getSimpleName());
        this.customer = customer;
        this.transactions = transactions;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    @Override
    public String getSummary() {
        return "Statement for " + customer.getFullName() +
                " | Period: " + periodStart + " to " + periodEnd +
                " | Closing Balance: " + closingBalance;
    }
}
