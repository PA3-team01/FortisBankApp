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
            writer.write("Report ID:," + csvEscape(report.getReportId()) + "\n");
            writer.write("Generated:," + csvEscape(report.getGeneratedDate().toString()) + "\n");
            writer.write("Customer:," + csvEscape(report.getCustomer().getFullName()) + "\n");
            writer.write("Period:," + csvEscape(report.getPeriodStart().toString()) + " to " + csvEscape(report.getPeriodEnd().toString()) + "\n");
            writer.write("Opening Balance:," + report.getOpeningBalance() + "\n");
            writer.write("Closing Balance:," + report.getClosingBalance() + "\n\n");

            writer.write("TransactionNumber,Date,Type,Description,SignedAmount\n");

            for (Transaction t : report.getTransactions()) {
                BigDecimal signed = BigDecimal.ZERO;

                for (Account account : customerAccounts) {
                    signed = t.getSignedAmountFor(account);
                    if (signed.compareTo(BigDecimal.ZERO) != 0) {
                        break;
                    }
                }

                writer.write(String.join(",",
                        csvEscape(t.getTransactionNumber()),
                        csvEscape(t.getTransactionDate().toString()),
                        csvEscape(t.getTransactionType().name()),
                        csvEscape(t.getDescription()),
                        signed.toString()
                ));
                writer.write("\n");
            }
        }
    }

    public static void exportBankSummaryToCSV(BankSummaryReport report, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Bank Summary Report\n");
            writer.write("Report ID:," + csvEscape(report.getReportId()) + "\n");
            writer.write("Generated:," + csvEscape(report.getGeneratedDate().toString()) + "\n");
            writer.write("Summary:," + csvEscape(report.getSummary()) + "\n\n");

            writer.write("Total Customers:," + report.getTotalCustomers() + "\n");
            writer.write("Total Accounts:," + report.getTotalAccounts() + "\n");
            writer.write("Total Balance:," + report.getTotalBalance() + "\n");
            writer.write("Total Credit Used:," + report.getTotalCreditUsed() + "\n");
            writer.write("Total Fees Collected:," + report.getTotalFeesCollected() + "\n\n");

            writer.write("Accounts by Type:\nType,Count\n");
            report.getAccountTypeCounts().forEach((type, count) -> {
                try {
                    writer.write(csvEscape(type) + "," + count + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.write("\nLow Balance Accounts (< $50):\nAccountNumber,Customer,Balance\n");
            for (Account acc : report.getLowBalanceAccounts()) {
                writer.write(String.join(",",
                        csvEscape(acc.getAccountNumber()),
                        csvEscape(acc.getCustomer().getFullName()),
                        acc.getAvailableBalance().toString()
                ));
                writer.write("\n");
            }
        }
    }

    // Escapes special characters for CSV compatibility
    private static String csvEscape(String input) {
        if (input == null) return "";
        return "\"" + input.replace("\"", "\"\"") + "\"";
    }
}
