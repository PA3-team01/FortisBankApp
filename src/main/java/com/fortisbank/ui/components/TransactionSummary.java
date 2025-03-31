package com.fortisbank.ui.components;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;

public class TransactionSummary extends JPanel {

    public TransactionSummary(Account account) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel title = new JLabel("Recent Transactions");
        StyleUtils.styleLabel(title);
        add(title);
        add(Box.createVerticalStrut(5));

        // TODO: Replace this with a call to TransactionService.getRecentTransactions(account)
        TransactionList recentTxs = new TransactionList(); // Placeholder for recent transactions

        if (recentTxs.isEmpty()) {
            JLabel none = new JLabel("No recent transactions.");
            StyleUtils.styleLabel(none);
            add(none);
        } else {
            for (Transaction tx : recentTxs) {
                JLabel txLabel = new JLabel("• [" + tx.getTransactionDate() + "] " +
                        tx.getTransactionType() + " — $" + String.format("%.2f", tx.getAmount()));
                StyleUtils.styleLabel(txLabel);
                add(txLabel);
                add(Box.createVerticalStrut(4));
            }
        }
    }
}
