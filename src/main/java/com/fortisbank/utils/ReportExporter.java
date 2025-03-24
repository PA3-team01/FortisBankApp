package com.fortisbank.utils;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.reports.BankSummaryReport;
import com.fortisbank.models.reports.CustomerStatementReport;
import com.fortisbank.models.transactions.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class ReportExporter {

    public static void exportCustomerStatementToCSV(CustomerStatementReport report, String filePath, AccountList customerAccounts) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Customer Statement Report\n");
            writer.write("Report ID:," + report.getReportId() + "\n");
            writer.write("Generated:," + report.getGeneratedDate() + "\n");
            writer.write("Customer:," + report.getCustomer().getFullName() + "\n");
            writer.write("Period:," + report.getPeriodStart() + " to " + report.getPeriodEnd() + "\n");
            writer.write("Opening Balance:," + report.getOpeningBalance() + "\n");
            writer.write("Closing Balance:," + report.getClosingBalance() + "\n\n");

            writer.write("TransactionNumber,Date,Type,Description,SignedAmount\n");

            for (Transaction t : report.getTransactions()) {
                BigDecimal signed = BigDecimal.ZERO;

                for (Account account : customerAccounts) {
                    signed = t.getSignedAmountFor(account);
                    if (signed.compareTo(BigDecimal.ZERO) != 0) {
                        break; // We found the matching context account
                    }
                }

                writer.write(String.join(",",
                        t.getTransactionNumber(),
                        t.getTransactionDate().toString(),
                        t.getTransactionType().name(),
                        t.getDescription().replace(",", " "),
                        signed.toString()
                ));
                writer.write("\n");
            }
        }
    }


    public static void exportBankSummaryToCSV(BankSummaryReport report, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Bank Summary Report\n");
            writer.write("Report ID:," + report.getReportId() + "\n");
            writer.write("Generated:," + report.getGeneratedDate() + "\n");
            writer.write("Summary:," + report.getSummary() + "\n\n");

            writer.write("Total Customers:," + report.getTotalCustomers() + "\n");
            writer.write("Total Accounts:," + report.getTotalAccounts() + "\n");
            writer.write("Total Balance:," + report.getTotalBalance() + "\n");
            writer.write("Total Credit Used:," + report.getTotalCreditUsed() + "\n");
            writer.write("Total Fees Collected:," + report.getTotalFeesCollected() + "\n\n");

            writer.write("Accounts by Type:\nType,Count\n");
            report.getAccountTypeCounts().forEach((type, count) -> {
                try {
                    writer.write(type + "," + count + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.write("\nLow Balance Accounts (< $50):\nAccountNumber,Customer,Balance\n");
            for (Account acc : report.getLowBalanceAccounts()) {
                writer.write(String.join(",",
                        acc.getAccountNumber(),
                        acc.getCustomer().getFullName(),
                        acc.getAvailableBalance().toString()
                ) + "\n");
            }
        }
    }
}
