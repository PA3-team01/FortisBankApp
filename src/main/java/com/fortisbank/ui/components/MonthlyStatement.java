package com.fortisbank.ui.components;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MonthlyStatement extends JPanel {

    public MonthlyStatement(Account account, TransactionList transactions) {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === HEADER ===
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

        JLabel period = new JLabel("Period: [Selected Month] [Selected Year]");
        StyleUtils.styleLabel(period);
        header.add(period);

        // === TODO: Implement month navigation buttons ===
        // JPanel navButtons = new JPanel(); ...

        add(header, BorderLayout.NORTH);

        // === TRANSACTIONS TABLE/VIEW ===
        JPanel txPanel = new JPanel();
        txPanel.setLayout(new BoxLayout(txPanel, BoxLayout.Y_AXIS));
        txPanel.setOpaque(false);

        TransactionList txs = transactions; // Placeholder for transactions

        if (txs.isEmpty()) {
            JLabel emptyLabel = new JLabel("No transactions for this period.");
            StyleUtils.styleLabel(emptyLabel);
            txPanel.add(emptyLabel);
        } else {
            for (Transaction tx : txs) {
                JLabel txLabel = new JLabel("• [" + tx.getTransactionDate() + "] " + tx.getTransactionType()
                        + " — $" + String.format("%.2f", tx.getAmount()));
                StyleUtils.styleLabel(txLabel);
                txPanel.add(txLabel);
                txPanel.add(Box.createVerticalStrut(4));
            }
        }

        add(txPanel, BorderLayout.CENTER);
    }
}
