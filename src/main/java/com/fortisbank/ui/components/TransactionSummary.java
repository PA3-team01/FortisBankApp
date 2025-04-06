package com.fortisbank.ui.components;

import com.fortisbank.business.services.transaction.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.TransactionList;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;

/**
 * The TransactionSummary class is a JPanel component that displays a summary
 * of recent transactions for a given account.
 */
public class TransactionSummary extends JPanel {

    private TransactionService transactionService;
    private StorageMode storageMode;
    private TransactionList transactionList;

    /**
     * Constructs a TransactionSummary panel for the given account and storage mode.
     *
     * @param account the account to display transactions for
     * @param storageMode the storage mode to use for transaction services
     */
    public TransactionSummary(Account account, StorageMode storageMode) {
        this.storageMode = storageMode;
        this.transactionService = TransactionService.getInstance(storageMode);
        this.transactionList = transactionService.getRecentTransactionsByAccount(account);
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