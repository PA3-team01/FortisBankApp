package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.users.Customer;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.components.TransactionSummary;
import com.fortisbank.ui.forms.DepositForm;
import com.fortisbank.ui.forms.TransferForm;
import com.fortisbank.ui.forms.WithdrawalForm;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends JPanel {

    private final JComboBox<Account> accountSelector = new JComboBox<>();
    private final JPanel previewPanel = new JPanel();
    private StorageMode storageMode;

    public TransactionPanel(StorageMode storageMode) {
        this.storageMode = storageMode;
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
            Account selected = (Account) accountSelector.getSelectedItem();
            if (selected != null) {
                new DepositForm(selected,storageMode).setVisible(true);
            }
        });

        withdrawBtn.addActionListener(e -> {
            Account selected = (Account) accountSelector.getSelectedItem();
            if (selected != null) {
                new WithdrawalForm(selected,storageMode).setVisible(true);
            }
        });

        transferBtn.addActionListener(e -> {
            Account selected = (Account) accountSelector.getSelectedItem();
            if (selected != null) {
                new TransferForm(selected,storageMode).setVisible(true);
            }
        });
    }

    private void populateAccountSelector() {
        Customer customer = SessionManager.getCustomer();
        if (customer != null && customer.getAccounts() != null) {
            for (Account acc : customer.getAccounts()) {
                accountSelector.addItem(acc);
            }
        }
    }

    private void updateTransactionPreview() {
        previewPanel.removeAll();

        previewPanel.add(Box.createVerticalStrut(10));
        previewPanel.add(new JLabel("Recent Transactions"));
        previewPanel.add(Box.createVerticalStrut(5));

        Account selected = (Account) accountSelector.getSelectedItem();
        if (selected != null) {
            TransactionSummary summary = new TransactionSummary(selected);
            previewPanel.add(summary);
        } else {
            JLabel warning = new JLabel("No account selected.");
            StyleUtils.styleLabel(warning);
            previewPanel.add(warning);
        }

        previewPanel.revalidate();
        previewPanel.repaint();
    }
}
