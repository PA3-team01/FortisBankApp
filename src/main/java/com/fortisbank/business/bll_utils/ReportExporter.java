package com.fortisbank.business.bll_utils;

import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.collections.AccountList;
import com.fortisbank.contracts.models.reports.BankSummaryReport;
import com.fortisbank.contracts.models.reports.CustomerStatementReport;
import com.fortisbank.contracts.models.transactions.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Utility class for exporting reports to CSV files.
 */
public class ReportExporter {

    /**
     * Exports a customer statement report to a CSV file.
     *
     * @param report the customer statement report to export
     * @param filePath the file path to save the CSV file
     * @param customerAccounts the list of customer accounts
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Exports a bank summary report to a CSV file.
     *
     * @param report the bank summary report to export
     * @param filePath the file path to save the CSV file
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Escapes special characters for CSV compatibility.
     *
     * @param input the input string to escape
     * @return the escaped string
     */
    private static String csvEscape(String input) {
        if (input == null) return "";
        return "\"" + input.replace("\"", "\"\"") + "\"";
    }
}