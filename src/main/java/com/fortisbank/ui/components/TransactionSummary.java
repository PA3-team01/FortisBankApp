package com.fortisbank.ui.components;

import com.fortisbank.business.services.transaction.TransactionService;
import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.collections.TransactionList;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The TransactionSummary class is a JPanel component that displays a summary
 * of recent transactions for a given account.
 */
public class TransactionSummary extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(TransactionSummary.class.getName());

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
        try {
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing TransactionSummary: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to load transaction summary: " + e.getMessage());
        }
    }
}