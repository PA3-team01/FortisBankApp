package com.fortisbank.ui.components;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.ui.frames.subFrames.MonthlyStatementFrame;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class AccountInfo extends JPanel {

    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;

    public AccountInfo(Account account) {
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
        TransactionSummary summary = new TransactionSummary(account); // Optional: pass account
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

            // TODO: Replace with real filtered data
            TransactionList filtered = new TransactionList(); // Fill this with filtered transactions

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
