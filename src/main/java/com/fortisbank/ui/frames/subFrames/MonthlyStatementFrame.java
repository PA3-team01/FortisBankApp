package com.fortisbank.ui.frames.subFrames;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class MonthlyStatementFrame extends JFrame {

    public MonthlyStatementFrame(Account account, TransactionList transactions, String month, int year) {
        // === Frame Setup ===
        setUndecorated(true);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);
        StyleUtils.applyGlobalFrameStyle(this);

        // === Top Custom Title Bar ===
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Monthly Statement", null);
        add(titleBar, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel container = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(container);

        // === HEADER INFO ===
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Monthly Statement");
        StyleUtils.styleFormTitle(title);
        header.add(title);
        header.add(Box.createVerticalStrut(8));

        JLabel accountInfo = new JLabel("Account: " + account.getAccountType()
                + " (" + account.getAccountNumber() + ") — Balance: $" + String.format("%.2f", account.getAvailableBalance()));
        StyleUtils.styleLabel(accountInfo);
        header.add(accountInfo);

        JLabel period = new JLabel("Period: " + month + " " + year);
        StyleUtils.styleLabel(period);
        header.add(period);

        // === TODO: Add month navigation buttons ===

        container.add(header, BorderLayout.NORTH);

        // === TRANSACTIONS ===
        JPanel txPanel = new JPanel();
        txPanel.setLayout(new BoxLayout(txPanel, BoxLayout.Y_AXIS));
        txPanel.setOpaque(false);

        if (transactions == null || transactions.isEmpty()) {
            JLabel emptyLabel = new JLabel("No transactions for this period.");
            StyleUtils.styleLabel(emptyLabel);
            txPanel.add(emptyLabel);
        } else {
            for (Transaction tx : transactions) {
                JLabel txLabel = new JLabel("• [" + tx.getTransactionDate() + "] "
                        + tx.getTransactionType() + " — $" + String.format("%.2f", tx.getAmount()));
                StyleUtils.styleLabel(txLabel);
                txPanel.add(txLabel);
                txPanel.add(Box.createVerticalStrut(4));
            }
        }

        JScrollPane scrollPane = new JScrollPane(txPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scrollPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);

        setVisible(true);
    }
}
