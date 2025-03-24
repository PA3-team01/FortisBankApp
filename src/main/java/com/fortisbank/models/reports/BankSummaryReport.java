package com.fortisbank.models.reports;

import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;

import java.math.BigDecimal;
import java.util.Map;

public class BankSummaryReport extends Report {

    private final int totalCustomers;
    private final int totalAccounts;
    private final Map<String, Long> accountTypeCounts;
    private final BigDecimal totalBalance;
    private final BigDecimal totalCreditUsed;
    private final BigDecimal totalFeesCollected;
    private final TransactionList allTransactions;
    private final AccountList lowBalanceAccounts;

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

    @Override
    public String getSummary() {
        return "Bank Summary: " +
                totalCustomers + " customers, " +
                totalAccounts + " accounts, " +
                "Total Balance: " + totalBalance;
    }

    // Getters and setters

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public Map<String, Long> getAccountTypeCounts() {
        return accountTypeCounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public BigDecimal getTotalCreditUsed() {
        return totalCreditUsed;
    }

    public BigDecimal getTotalFeesCollected() {
        return totalFeesCollected;
    }

    public TransactionList getAllTransactions() {
        return allTransactions;
    }

    public AccountList getLowBalanceAccounts() {
        return lowBalanceAccounts;
    }

}
