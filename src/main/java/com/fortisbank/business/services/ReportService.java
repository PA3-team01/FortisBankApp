package com.fortisbank.business.services;

import com.fortisbank.data.repositories.IAccountRepository;
import com.fortisbank.data.repositories.ICustomerRepository;
import com.fortisbank.data.repositories.ITransactionRepository;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.models.Customer;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.AccountType;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.reports.BankSummaryReport;
import com.fortisbank.models.reports.CustomerStatementReport;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.utils.ReportExporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportService {

    private final ICustomerRepository customerRepository;
    private final IAccountRepository accountRepository;
    private final ITransactionRepository transactionRepository;

    public ReportService(RepositoryFactory factory) {
        this.customerRepository = factory.getCustomerRepository();
        this.accountRepository = factory.getAccountRepository();
        this.transactionRepository = factory.getTransactionRepository();
    }

    /**
     * Generates a monthly statement for a specific customer.
     */
    public CustomerStatementReport generateCustomerStatement(Customer customer, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        TransactionList transactions = transactionRepository
                .getTransactionsByCustomerAndDateRange(customer.getCustomerID(), start, end);

        BigDecimal openingBalance = transactionRepository.getBalanceBeforeDate(customer.getCustomerID(), start);

        // Fetch all customer accounts (not just IDs!)
        AccountList customerAccounts = accountRepository
                .getAccountsByCustomerId(customer.getCustomerID());

        BigDecimal closingBalance = openingBalance;

        for (Transaction t : transactions) {
            for (Account acc : customerAccounts) {
                BigDecimal signed = t.getSignedAmountFor(acc);
                if (signed.compareTo(BigDecimal.ZERO) != 0) {
                    closingBalance = closingBalance.add(signed);
                    break; // Stop after finding the matching context
                }
            }
        }

        return new CustomerStatementReport(customer, transactions, openingBalance, closingBalance, start, end);
    }


    /**
     * Generates a full bank-wide summary report.
     */
    public BankSummaryReport generateBankSummaryReport() {
        var customers = customerRepository.getAllCustomers();
        var accounts = accountRepository.getAllAccounts();
        var transactions = transactionRepository.getAllTransactions();

        Map<String, Long> accountTypeCounts = accounts.stream()
                .collect(Collectors.groupingBy(
                        acc -> acc.getAccountType().name(),
                        Collectors.counting()
                ));

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getAvailableBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditUsed = accounts.stream()
                .filter(acc -> acc.getAccountType() == AccountType.CREDIT)
                .map(Account::getCreditLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFees = transactions.stream()
                .filter(t -> t.getTransactionType() == com.fortisbank.models.transactions.TransactionType.FEE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AccountList lowBalanceAccounts = new AccountList(
                accounts.stream()
                        .filter(acc -> acc.getAvailableBalance().compareTo(new BigDecimal("50")) < 0)
                        .collect(Collectors.toList())
        );

        return new BankSummaryReport(
                customers.size(),
                accounts.size(),
                accountTypeCounts,
                totalBalance,
                totalCreditUsed,
                totalFees,
                transactions,
                lowBalanceAccounts
        );
    }

    public void saveCustomerStatementReportToCSV(CustomerStatementReport report, String filePath) {
        try {
            AccountList customerAccounts = accountRepository
                    .getAccountsByCustomerId(report.getCustomer().getCustomerID());

            ReportExporter.exportCustomerStatementToCSV(report, filePath, customerAccounts);
        } catch (IOException e) {
            System.err.println("Error exporting customer report: " + e.getMessage());
        }
    }


    public void saveBankSummaryReportToCSV(BankSummaryReport report, String filePath) {
        try {
            ReportExporter.exportBankSummaryToCSV(report, filePath);
        } catch (IOException e) {
            System.err.println("Error exporting bank report: " + e.getMessage());
        }
    }
}
