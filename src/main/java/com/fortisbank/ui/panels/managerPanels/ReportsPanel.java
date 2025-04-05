package com.fortisbank.ui.panels.managerPanels;

import com.fortisbank.business.services.report.ReportService;
import com.fortisbank.data.repositories.RepositoryFactory;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.reports.BankSummaryReport;
import com.fortisbank.models.reports.CustomerStatementReport;
import com.fortisbank.models.users.Customer;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.YearMonth;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final StorageMode storageMode;
    private final ReportService reportService;
    private final JComboBox<String> reportTypeSelector = new JComboBox<>(new String[]{"Bank Summary", "Customer Statement"});
    private final JComboBox<Customer> customerSelector = new JComboBox<>();
    private final JComboBox<YearMonth> monthSelector = new JComboBox<>();
    private final JTable previewTable = new JTable();
    private final JButton generateBtn = new JButton("Generate Report");
    private final JButton downloadBtn = new JButton("Download CSV");

    public ReportsPanel(StorageMode storageMode, List<Customer> allCustomers) {
        this.storageMode = storageMode;
        this.reportService = new ReportService(RepositoryFactory.getInstance(storageMode));

        setLayout(new BorderLayout());
        setBackground(StyleUtils.BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(StyleUtils.BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel reportLabel = new JLabel("Report Type:");
        StyleUtils.styleLabel(reportLabel);
        StyleUtils.styleDropdown(reportTypeSelector);
        StyleUtils.styleDropdown(customerSelector);
        StyleUtils.styleDropdown(monthSelector);
        StyleUtils.styleButton(generateBtn, true);
        StyleUtils.styleButton(downloadBtn, false);

        for (Customer c : allCustomers) customerSelector.addItem(c);
        for (int i = 0; i < 12; i++) monthSelector.addItem(YearMonth.now().minusMonths(i));

        topPanel.add(reportLabel);
        topPanel.add(reportTypeSelector);
        topPanel.add(customerSelector);
        topPanel.add(monthSelector);
        topPanel.add(generateBtn);
        topPanel.add(downloadBtn);

        previewTable.setFillsViewportHeight(true);
        previewTable.setBackground(StyleUtils.NAVBAR_BUTTON_COLOR);
        previewTable.setForeground(StyleUtils.TEXT_COLOR);
        previewTable.setFont(StyleUtils.FIELD_FONT);
        previewTable.setRowHeight(24);
        previewTable.getTableHeader().setFont(StyleUtils.BUTTON_FONT);
        previewTable.getTableHeader().setBackground(StyleUtils.NAVBAR_BG);
        previewTable.getTableHeader().setForeground(StyleUtils.TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(previewTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleUtils.NAVBAR_BG));
        scrollPane.getViewport().setBackground(StyleUtils.BACKGROUND_COLOR);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        customerSelector.setVisible(false);
        monthSelector.setVisible(false);

        reportTypeSelector.addActionListener(e -> {
            boolean isCustomer = reportTypeSelector.getSelectedItem().equals("Customer Statement");
            customerSelector.setVisible(isCustomer);
            monthSelector.setVisible(isCustomer);
        });

        generateBtn.addActionListener(e -> generateReport());
        downloadBtn.addActionListener(e -> downloadReport());
    }

    private Object currentReport;

    private void generateReport() {
        if (reportTypeSelector.getSelectedItem().equals("Bank Summary")) {
            BankSummaryReport report = reportService.generateBankSummaryReport();
            currentReport = report;
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Summary");
            model.addRow(new Object[]{report.getSummary()});
            model.addRow(new Object[]{"Total Customers: " + report.getTotalCustomers()});
            model.addRow(new Object[]{"Total Accounts: " + report.getTotalAccounts()});
            model.addRow(new Object[]{"Total Balance: " + report.getTotalBalance()});
            model.addRow(new Object[]{"Total Credit Used: " + report.getTotalCreditUsed()});
            model.addRow(new Object[]{"Total Fees Collected: " + report.getTotalFeesCollected()});
            previewTable.setModel(model);
        } else {
            Customer selectedCustomer = (Customer) customerSelector.getSelectedItem();
            YearMonth selectedMonth = (YearMonth) monthSelector.getSelectedItem();
            if (selectedCustomer == null || selectedMonth == null) return;
            CustomerStatementReport report = reportService.generateCustomerStatement(selectedCustomer, selectedMonth);
            currentReport = report;
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Transaction", "Date", "Type", "Description", "Amount"}, 0
            );
            report.getTransactions().forEach(t -> {
                model.addRow(new Object[]{
                        t.getTransactionNumber(),
                        t.getTransactionDate(),
                        t.getTransactionType(),
                        t.getDescription(),
                        t.getAmount()
                });
            });
            previewTable.setModel(model);
        }
    }

    private void downloadReport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (currentReport instanceof BankSummaryReport summary) {
                reportService.saveBankSummaryReportToCSV(summary, file.getAbsolutePath());
            } else if (currentReport instanceof CustomerStatementReport customerReport) {
                reportService.saveCustomerStatementReportToCSV(customerReport, file.getAbsolutePath());
            }
        }
    }
}
