package com.fortisbank.models.reports;

import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.users.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Class representing a customer statement report.
 */
public class CustomerStatementReport extends Report {

    /**
     * The customer for whom the statement is generated.
     */
    private final Customer customer;

    /**
     * The list of transactions included in the statement.
     */
    private final List<Transaction> transactions;

    /**
     * The opening balance at the start of the statement period.
     */
    private final BigDecimal openingBalance;

    /**
     * The closing balance at the end of the statement period.
     */
    private final BigDecimal closingBalance;

    /**
     * The start date of the statement period.
     */
    private final LocalDate periodStart;

    /**
     * The end date of the statement period.
     */
    private final LocalDate periodEnd;

    /**
     * Constructor initializing the customer statement report with specified values.
     *
     * @param customer the customer for whom the statement is generated
     * @param transactions the list of transactions included in the statement
     * @param openingBalance the opening balance at the start of the statement period
     * @param closingBalance the closing balance at the end of the statement period
     * @param periodStart the start date of the statement period
     * @param periodEnd the end date of the statement period
     */
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

    /**
     * Returns the customer for whom the statement is generated.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the list of transactions included in the statement.
     *
     * @return the list of transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Returns the opening balance at the start of the statement period.
     *
     * @return the opening balance
     */
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    /**
     * Returns the closing balance at the end of the statement period.
     *
     * @return the closing balance
     */
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    /**
     * Returns the start date of the statement period.
     *
     * @return the start date of the statement period
     */
    public LocalDate getPeriodStart() {
        return periodStart;
    }

    /**
     * Returns the end date of the statement period.
     *
     * @return the end date of the statement period
     */
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Returns a summary of the customer statement report.
     *
     * @return a string containing the summary of the customer statement report
     */
    @Override
    public String getSummary() {
        return "Statement for " + customer.getFullName() +
                " | Period: " + periodStart + " to " + periodEnd +
                " | Closing Balance: " + closingBalance;
    }
}