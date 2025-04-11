package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.business.services.session.SessionManager;
import com.fortisbank.ui.components.TransactionSummary;
import com.fortisbank.ui.forms.DepositForm;
import com.fortisbank.ui.forms.TransferForm;
import com.fortisbank.ui.forms.WithdrawalForm;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(TransactionPanel.class.getName());
    private final JComboBox<Account> accountSelector = new JComboBox<>();
    private final JPanel previewPanel = new JPanel();
    private StorageMode storageMode;

    public TransactionPanel(StorageMode storageMode) {
        this.storageMode = storageMode;

        try {
            setLayout(new BorderLayout());
            StyleUtils.styleFormPanel(this);

            // === Title ===
            JLabel title = new JLabel("Manage Your Transactions");
            StyleUtils.styleFormTitle(title);
            add(title, BorderLayout.NORTH);

            // === Account Selector ===
            JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectorPanel.setOpaque(false);

            JLabel accountLabel = new JLabel("Select Account:");
            StyleUtils.styleLabel(accountLabel);

            populateAccountSelector();
            accountSelector.addActionListener(e -> updateTransactionPreview());
            StyleUtils.styleDropdown(accountSelector);

            // Custom renderer for styled display
            accountSelector.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (value instanceof Account acc) {
                        label.setText("[" + acc.getAccountType() + "] — $" + String.format("%.2f", acc.getAvailableBalance()));
                    } else {
                        label.setText("Select an account");
                    }

                    StyleUtils.styleLabel(label);
                    return label;
                }
            });

            selectorPanel.add(accountLabel);
            selectorPanel.add(accountSelector);

            // === Action Buttons ===
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setOpaque(false);

            JButton depositBtn = new JButton("Deposit");
            JButton withdrawBtn = new JButton("Withdraw");
            JButton transferBtn = new JButton("Transfer");

            StyleUtils.styleButton(depositBtn, true);
            StyleUtils.styleButton(withdrawBtn, true);
            StyleUtils.styleButton(transferBtn, true);

            buttonPanel.add(depositBtn);
            buttonPanel.add(withdrawBtn);
            buttonPanel.add(transferBtn);

            // === Transactions Preview Panel ===
            previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
            previewPanel.setOpaque(false);
            previewPanel.add(Box.createVerticalStrut(10));
            previewPanel.add(new JLabel("Recent Transactions"));
            previewPanel.add(Box.createVerticalStrut(5));
            updateTransactionPreview(); // Show default preview

            // === Center Layout ===
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.add(selectorPanel);
            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(buttonPanel);
            centerPanel.add(Box.createVerticalStrut(20));
            centerPanel.add(previewPanel);

            add(centerPanel, BorderLayout.CENTER);

            // === Button Actions ===
            depositBtn.addActionListener(e -> {
                try {
                    Account selected = (Account) accountSelector.getSelectedItem();
                    if (selected != null) {
                        new DepositForm(selected, storageMode).setVisible(true);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening DepositForm: {0}", ex.getMessage());
                    StyleUtils.showStyledErrorDialog(this, "Failed to open deposit form: " + ex.getMessage());
                }
            });

            withdrawBtn.addActionListener(e -> {
                try {
                    Account selected = (Account) accountSelector.getSelectedItem();
                    if (selected != null) {
                        new WithdrawalForm(selected, storageMode).setVisible(true);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening WithdrawalForm: {0}", ex.getMessage());
                    StyleUtils.showStyledErrorDialog(this, "Failed to open withdrawal form: " + ex.getMessage());
                }
            });

            transferBtn.addActionListener(e -> {
                try {
                    Account selected = (Account) accountSelector.getSelectedItem();
                    if (selected != null) {
                        new TransferForm(selected, storageMode).setVisible(true);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening TransferForm: {0}", ex.getMessage());
                    StyleUtils.showStyledErrorDialog(this, "Failed to open transfer form: " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing TransactionPanel: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to initialize the transaction panel: " + e.getMessage());
        }
    }

    private void populateAccountSelector() {
        try {
            Customer customer = SessionManager.getCustomer();
            if (customer != null && customer.getAccounts() != null) {
                for (Account acc : customer.getAccounts()) {
                    accountSelector.addItem(acc);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error populating account selector: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to load accounts: " + e.getMessage());
        }
    }

    private void updateTransactionPreview() {
        try {
            previewPanel.removeAll();

            previewPanel.add(Box.createVerticalStrut(10));
            previewPanel.add(new JLabel("Recent Transactions"));
            previewPanel.add(Box.createVerticalStrut(5));

            Account selected = (Account) accountSelector.getSelectedItem();
            if (selected != null) {
                TransactionSummary summary = new TransactionSummary(selected, storageMode);
                previewPanel.add(summary);
            } else {
                JLabel warning = new JLabel("No account selected.");
                StyleUtils.styleLabel(warning);
                previewPanel.add(warning);
            }

            previewPanel.revalidate();
            previewPanel.repaint();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating transaction preview: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to update transaction preview: " + e.getMessage());
        }
    }
}