package com.fortisbank.ui.components;

import com.fortisbank.business.services.ITransactionService;
import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;

public class TransactionSummary extends JPanel {

    private TransactionService transactionService;
    private StorageMode storageMode;
    private TransactionList transactionList;

    public TransactionSummary(Account account, StorageMode storageMode) {
        this.storageMode = storageMode;
        this.transactionService = TransactionService.getInstance(storageMode);
        this.transactionList = transactionService.getRecentTransactionsByAccount(account, 5);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel title = new JLabel("Recent Transactions");
        StyleUtils.styleLabel(title);
        add(title);
        add(Box.createVerticalStrut(5));

        if (transactionList.isEmpty()) {
            JLabel none = new JLabel("No recent transactions.");
            StyleUtils.styleLabel(none);
            add(none);
        } else {
            for (Transaction tx : transactionList) {
                JLabel txLabel = new JLabel("• [" + tx.getTransactionDate() + "] " +
                        tx.getTransactionType() + " — $" + String.format("%.2f", tx.getAmount()));
                StyleUtils.styleLabel(txLabel);
                add(txLabel);
                add(Box.createVerticalStrut(4));
            }
        }
    }
}
