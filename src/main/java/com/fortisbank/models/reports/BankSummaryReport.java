package com.fortisbank.models.reports;

import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Class representing a bank summary report.
 */
public class BankSummaryReport extends Report {

    /**
     * The total number of customers.
     */
    private final int totalCustomers;

    /**
     * The total number of accounts.
     */
    private final int totalAccounts;

    /**
     * A map containing the count of each account type.
     */
    private final Map<String, Long> accountTypeCounts;

    /**
     * The total balance across all accounts.
     */
    private final BigDecimal totalBalance;

    /**
     * The total credit used across all accounts.
     */
    private final BigDecimal totalCreditUsed;

    /**
     * The total fees collected.
     */
    private final BigDecimal totalFeesCollected;

    /**
     * A list of all transactions.
     */
    private final TransactionList allTransactions;

    /**
     * A list of accounts with low balances.
     */
    private final AccountList lowBalanceAccounts;

    /**
     * Constructor initializing the bank summary report with specified values.
     *
     * @param totalCustomers the total number of customers
     * @param totalAccounts the total number of accounts
     * @param accountTypeCounts a map containing the count of each account type
     * @param totalBalance the total balance across all accounts
     * @param totalCreditUsed the total credit used across all accounts
     * @param totalFeesCollected the total fees collected
     * @param allTransactions a list of all transactions
     * @param lowBalanceAccounts a list of accounts with low balances
     */
    public BankSummaryReport(
            int totalCustomers,
            int totalAccounts,
            Map<String, Long> accountTypeCounts,
            BigDecimal totalBalance,
            BigDecimal totalCreditUsed,
            BigDecimal totalFeesCollected,
            TransactionList allTransactions,
            AccountList lowBalanceAccounts
    ) {
        super("Bank Summary");
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.accountTypeCounts = accountTypeCounts;
        this.totalBalance = totalBalance;
        this.totalCreditUsed = totalCreditUsed;
        this.totalFeesCollected = totalFeesCollected;
        this.allTransactions = allTransactions;
        this.lowBalanceAccounts = lowBalanceAccounts;
    }

    /**
     * Returns a summary of the bank report.
     *
     * @return a string containing the summary of the bank report
     */
    @Override
    public String getSummary() {
        return "Bank Summary: " +
                totalCustomers + " customers, " +
                totalAccounts + " accounts, " +
                "Total Balance: " + totalBalance;
    }

    // Getters and setters

    /**
     * Returns the total number of customers.
     *
     * @return the total number of customers
     */
    public int getTotalCustomers() {
        return totalCustomers;
    }

    /**
     * Returns the total number of accounts.
     *
     * @return the total number of accounts
     */
    public int getTotalAccounts() {
        return totalAccounts;
    }

    /**
     * Returns a map containing the count of each account type.
     *
     * @return a map containing the count of each account type
     */
    public Map<String, Long> getAccountTypeCounts() {
        return accountTypeCounts;
    }

    /**
     * Returns the total balance across all accounts.
     *
     * @return the total balance across all accounts
     */
    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    /**
     * Returns the total credit used across all accounts.
     *
     * @return the total credit used across all accounts
     */
    public BigDecimal getTotalCreditUsed() {
        return totalCreditUsed;
    }

    /**
     * Returns the total fees collected.
     *
     * @return the total fees collected
     */
    public BigDecimal getTotalFeesCollected() {
        return totalFeesCollected;
    }

    /**
     * Returns a list of all transactions.
     *
     * @return a list of all transactions
     */
    public TransactionList getAllTransactions() {
        return allTransactions;
    }

    /**
     * Returns a list of accounts with low balances.
     *
     * @return a list of accounts with low balances
     */
    public AccountList getLowBalanceAccounts() {
        return lowBalanceAccounts;
    }

}