package com.fortisbank.ui.components;

import com.fortisbank.business.services.AccountService;
import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.frames.subFrames.MonthlyStatementFrame;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;

public class AccountInfo extends JPanel {

    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;
    private final TransactionService transactionService;
    private final StorageMode storageMode;

    public AccountInfo(Account account, StorageMode storageMode) {
        this.storageMode = storageMode;
        this.transactionService = TransactionService.getInstance(storageMode);

        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === Header: Account Info ===
        String infoHtml = "<html>" + account.displayAccountInfo().replace("\n", "<br>") + "</html>";
        JLabel title = new JLabel(infoHtml);
        StyleUtils.styleFormTitle(title);
        add(title, BorderLayout.NORTH);

        // === Center: Transaction Summary + Statement Controls ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        TransactionSummary summary = new TransactionSummary(account, storageMode);
        centerPanel.add(summary);

        // === Statement Controls ===
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);

        monthCombo = new JComboBox<>();
        yearCombo = new JComboBox<>();
        StyleUtils.styleDropdown(monthCombo);
        StyleUtils.styleDropdown(yearCombo);
        populateMonthYear();

        JButton statementBtn = new JButton("Get Statement");
        StyleUtils.styleButton(statementBtn, true);
        statementBtn.addActionListener(e -> generateStatement(account));

        controls.add(new JLabel("Month:"));
        controls.add(monthCombo);
        controls.add(new JLabel("Year:"));
        controls.add(yearCombo);
        controls.add(statementBtn);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(controls);

        // === Close Account Button ===
        if (account.isActive() && account.getAvailableBalance().compareTo(java.math.BigDecimal.ZERO) == 0) {
            JButton closeBtn = new JButton("Close Account");
            StyleUtils.styleButton(closeBtn, false);
            closeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            closeBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to close this account?",
                        "Confirm Closure",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        AccountService.getInstance(storageMode).closeAccount(account);
                        StyleUtils.showStyledSuccessDialog(this, "Account closed successfully.");
                        SwingUtilities.getWindowAncestor(this).dispose(); // refresh by closing and reopening panel if needed
                    } catch (Exception ex) {
                        StyleUtils.showStyledErrorDialog(this, ex.getMessage());
                    }
                }
            });

            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(closeBtn);
        }

        add(centerPanel, BorderLayout.CENTER);
    }

    private void populateMonthYear() {
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(String.format("%02d", i)); // "01" to "12"
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            yearCombo.addItem(currentYear - i); // Last 5 years
        }
    }

    private void generateStatement(Account account) {
        String selectedMonth = (String) monthCombo.getSelectedItem();
        int selectedYear = (int) yearCombo.getSelectedItem();
        String userId = SessionManager.getCurrentUser().getUserId();

        LocalDate startDate = LocalDate.of(selectedYear, Integer.parseInt(selectedMonth), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        TransactionList filtered = transactionService.getTransactionsByCustomerAndDateRange(userId, startDate, endDate);
        new MonthlyStatementFrame(account, filtered, selectedMonth, selectedYear);
    }
}
