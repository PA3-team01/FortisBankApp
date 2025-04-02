package com.fortisbank.ui.components;

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
    private TransactionService transactionService;

    public AccountInfo(Account account , StorageMode storageMode) {
        transactionService = TransactionService.getInstance(storageMode);
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // Header
        String infoHtml = "<html>" + account.displayAccountInfo().replace("\n", "<br>") + "</html>";
        JLabel title = new JLabel(infoHtml);
        StyleUtils.styleFormTitle(title);
        add(title, BorderLayout.NORTH);


        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // === Transaction summary ===
        TransactionSummary summary = new TransactionSummary(account,storageMode); // Optional: pass account
        centerPanel.add(summary);

        // === Statement controls ===
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);

        monthCombo = new JComboBox<>();
        StyleUtils.styleDropdown(monthCombo);
        yearCombo = new JComboBox<>();
        StyleUtils.styleDropdown(yearCombo);
        populateMonthYear();

        JButton statementBtn = new JButton("Get Statement");
        StyleUtils.styleButton(statementBtn, true);

        controls.add(new JLabel("Month:"));
        controls.add(monthCombo);
        controls.add(new JLabel("Year:"));
        controls.add(yearCombo);
        controls.add(statementBtn);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(controls);

        add(centerPanel, BorderLayout.CENTER);

        // TODO: Add statementBtn action with service integration later
        statementBtn.addActionListener(e -> {
            String selectedMonth = (String) monthCombo.getSelectedItem();
            int selectedYear = (int) yearCombo.getSelectedItem();
            // current user
            var userId = SessionManager.getCurrentUser().getUserId();
            // start and end date
            var startDate = LocalDate.of(selectedYear, Integer.parseInt(selectedMonth), 1);
            var endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());


            TransactionList filtered = transactionService.getTransactionsByCustomerAndDateRange(userId,startDate,endDate);

            new MonthlyStatementFrame(account, filtered, selectedMonth, selectedYear);
        });

    }

    private void populateMonthYear() {
        // Fill months
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(String.format("%02d", i)); // "01" to "12"
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 5; i++) {
            yearCombo.addItem(currentYear - i); // Last 5 years
        }
    }
}
