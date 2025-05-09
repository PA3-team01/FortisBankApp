package com.fortisbank.ui.panels.managerPanels;

import com.fortisbank.business.services.report.ReportService;
import com.fortisbank.data.dal_utils.RepositoryFactory;
import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.reports.BankSummaryReport;
import com.fortisbank.contracts.models.reports.CustomerStatementReport;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.YearMonth;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ReportsPanel class represents the reports panel of the Fortis Bank application.
 * It extends JPanel and provides a user interface to generate and download various reports.
 */
public class ReportsPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(ReportsPanel.class.getName());
    private final StorageMode storageMode;
    private final ReportService reportService;
    private final JComboBox<String> reportTypeSelector = new JComboBox<>(new String[]{"Bank Summary", "Customer Statement"});
    private final JComboBox<Customer> customerSelector = new JComboBox<>();
    private final JComboBox<YearMonth> monthSelector = new JComboBox<>();
    private final JTable previewTable = new JTable();
    private final JButton generateBtn = new JButton("Generate Report");
    private final JButton downloadBtn = new JButton("Download CSV");
    private Object currentReport;

    /**
     * Constructs a ReportsPanel with the specified storage mode and list of all customers.
     *
     * @param storageMode the storage mode to use for services
     * @param allCustomers the list of all customers
     */
    public ReportsPanel(StorageMode storageMode, List<Customer> allCustomers) {
        this.storageMode = storageMode;
        this.reportService = new ReportService(RepositoryFactory.getInstance(storageMode));

        try {
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

            for (Customer c : allCustomers) {
                customerSelector.addItem(c); // Add Customer objects directly
            }

            // Set a custom renderer to display the full name
            customerSelector.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Customer customer) {
                        value = customer.getFullName(); // Display full name
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing ReportsPanel: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to initialize the reports panel: " + e.getMessage());
        }
    }

    /**
     * Generates the selected report and displays it in the preview table.
     */
    private void generateReport() {
        try {
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
                if (selectedCustomer == null || selectedMonth == null) {
                    StyleUtils.showStyledErrorDialog(this, "Please select a customer and a month.");
                    return;
                }
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
            StyleUtils.showStyledSuccessDialog(this, "Report generated successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating report: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Downloads the currently generated report as a CSV file.
     */
    private void downloadReport() {
        try {
            if (currentReport == null) {
                StyleUtils.showStyledErrorDialog(this, "No report to download. Please generate a report first.");
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (currentReport instanceof BankSummaryReport summary) {
                    reportService.saveBankSummaryReportToCSV(summary, file.getAbsolutePath());
                } else if (currentReport instanceof CustomerStatementReport customerReport) {
                    reportService.saveCustomerStatementReportToCSV(customerReport, file.getAbsolutePath());
                }
                StyleUtils.showStyledSuccessDialog(this, "Report downloaded successfully.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error downloading report: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to download report: " + e.getMessage());
        }
    }
}