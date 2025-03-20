package com.fortisbank.models;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.IdGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reportId;
    private Date generatedDate;
    private String reportType;
    private Customer customer;
    private Account account;
    private List<Transaction> transactions;
    private BigDecimal totalBalance;
    private int transactionCount;

    public Report(String reportType, Customer customer, Account account, List<Transaction> transactions) {
        this.reportId = IdGenerator.generateId();
        this.generatedDate = new Date();
        this.reportType = reportType;
        this.customer = customer;
        this.account = account;
        this.transactions = transactions;
        this.totalBalance = calculateTotalBalance();
        this.transactionCount = transactions.size();
    }

    private BigDecimal calculateTotalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (account != null) {
            balance = account.getAvailableBalance();
        } else if (customer != null) {
            // Recupere les comptes si on veut inclure tout les comptes d'un client
        }
        return balance;
    }

    public String getReportId() {
        return reportId;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public String getReportType() {
        return reportType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Account getAccount() {
        return account;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", generatedDate=" + generatedDate +
                ", reportType='" + reportType + '\'' +
                ", customer=" + (customer != null ? customer.getFullName() : "N/A") +
                ", account=" + (account != null ? account.getAccountNumber() : "N/A") +
                ", transactionCount=" + transactionCount +
                ", totalBalance=" + totalBalance +
                '}';
    }
}
