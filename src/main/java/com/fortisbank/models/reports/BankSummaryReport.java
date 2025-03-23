package com.fortisbank.models.reports;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class BankSummaryReport extends Report {

    private final int totalCustomers;
    private final int totalAccounts;
    private final Map<String, Long> accountTypeCounts;
    private final BigDecimal totalBalance;
    private final BigDecimal totalCreditUsed;
    private final BigDecimal totalFeesCollected;
    private final List<Transaction> allTransactions;
    private final List<Account> lowBalanceAccounts;

    public BankSummaryReport(
            int totalCustomers,
            int totalAccounts,
            Map<String, Long> accountTypeCounts,
            BigDecimal totalBalance,
            BigDecimal totalCreditUsed,
            BigDecimal totalFeesCollected,
            List<Transaction> allTransactions,
            List<Account> lowBalanceAccounts
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

    @Override
    public String getSummary() {
        return "Bank Summary: " +
                totalCustomers + " customers, " +
                totalAccounts + " accounts, " +
                "Total Balance: " + totalBalance;
    }

    // Getters and setters

}
