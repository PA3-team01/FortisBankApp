package com.fortisbank.ui.forms;

import com.fortisbank.business.services.CustomerService;
import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.collections.CustomerList;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

public class TransferForm extends TransactionForm {

    private final Account sourceAccount;
    private final JComboBox<Customer> customerSelector = new JComboBox<>();
    private final JComboBox<Account> destinationSelector = new JComboBox<>();
    private final JRadioButton selfTransferBtn = new JRadioButton("My Accounts");
    private final JRadioButton otherTransferBtn = new JRadioButton("Another Customer");
    private final JPanel dynamicRecipientPanel = new JPanel();
    private final JTextField amountField = new JTextField();
    private final JTextField descriptionField = new JTextField();

    public TransferForm(Account sourceAccount, StorageMode storageMode) {
        super("Transfer Funds", storageMode);
        this.sourceAccount = sourceAccount;
        setSize(600, 300);

        setupTransferTypeSelector();
        setupCustomerDropdown();
        buildTransferPanel();
    }

    private void setupTransferTypeSelector() {
        ButtonGroup group = new ButtonGroup();
        group.add(selfTransferBtn);
        group.add(otherTransferBtn);

        selfTransferBtn.setSelected(true);

        selfTransferBtn.addActionListener(e -> showSelfTransferOptions());
        otherTransferBtn.addActionListener(e -> showOtherCustomerOptions());
        StyleUtils.styleRadioButton(selfTransferBtn);
        StyleUtils.styleRadioButton(otherTransferBtn);
    }

    private void setupCustomerDropdown() {
        CustomerList customers = CustomerService.getInstance(storageMode).getAllCustomers();

        for (Customer customer : customers) {
            customerSelector.addItem(customer);
        }
        StyleUtils.styleDropdown(customerSelector);

        customerSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer customer) {
                    setText(customer.getFullName());
                }
                return this;
            }
        });


    }



    private void buildTransferPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        radioPanel.add(selfTransferBtn);
        radioPanel.add(otherTransferBtn);

        dynamicRecipientPanel.setLayout(new BoxLayout(dynamicRecipientPanel, BoxLayout.Y_AXIS));
        dynamicRecipientPanel.setOpaque(false);

        topPanel.add(radioPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(dynamicRecipientPanel);

        getContentPane().add(topPanel, BorderLayout.CENTER);
        showSelfTransferOptions(); // default
    }

    private JPanel getAmountAndDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel amountLabel = new JLabel("Amount:");
        JLabel descLabel = new JLabel("Description:");
        StyleUtils.styleLabel(amountLabel);
        StyleUtils.styleLabel(descLabel);
        StyleUtils.styleTextField(amountField);
        StyleUtils.styleTextField(descriptionField);

        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descLabel);
        panel.add(descriptionField);

        return panel;
    }

    private void showSelfTransferOptions() {
        dynamicRecipientPanel.removeAll();
        destinationSelector.removeAllItems();

        Customer self = SessionManager.getCustomer();
        for (Account acc : self.getAccounts()) {
            if (!acc.getAccountNumber().equals(sourceAccount.getAccountNumber()) && acc.isActive()) {
                destinationSelector.addItem(acc);
            }
        }
        StyleUtils.styleDropdown(destinationSelector);
        JLabel label = new JLabel("Select Destination Account:");
        StyleUtils.styleLabel(label);

        dynamicRecipientPanel.add(label);
        dynamicRecipientPanel.add(destinationSelector);
        dynamicRecipientPanel.add(Box.createVerticalStrut(10));
        dynamicRecipientPanel.add(getAmountAndDescriptionPanel());
        dynamicRecipientPanel.revalidate();
        dynamicRecipientPanel.repaint();
    }

    private void showOtherCustomerOptions() {
        dynamicRecipientPanel.removeAll();

        JLabel customerLabel = new JLabel("Select Customer:");
        StyleUtils.styleLabel(customerLabel);

        dynamicRecipientPanel.add(customerLabel);
        dynamicRecipientPanel.add(customerSelector);
        dynamicRecipientPanel.add(Box.createVerticalStrut(10));
        dynamicRecipientPanel.add(getAmountAndDescriptionPanel());
        dynamicRecipientPanel.revalidate();
        dynamicRecipientPanel.repaint();
    }

    @Override
    protected boolean handleConfirm() {
        Customer selectedCustomer = (Customer) customerSelector.getSelectedItem();
        if (selectedCustomer == null) {
            StyleUtils.showStyledErrorDialog(this, "Please select a customer.");
            return false;
        }

        Account destination = selectedCustomer.getAccounts().stream()
                .filter(Account::isActive)
                .findFirst()
                .orElse(null);

        if (destination == null) {
            StyleUtils.showStyledErrorDialog(this, "The selected customer has no active accounts.");
            return false;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
        } catch (NumberFormatException e) {
            StyleUtils.showStyledErrorDialog(this, "Invalid amount format.");
            return false;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            StyleUtils.showStyledErrorDialog(this, "Amount must be positive.");
            return false;
        }

        String description = descriptionField.getText().trim();

        Transaction tx = TransactionFactory.createTransaction(
                TransactionType.TRANSFER,
                description,
                new Date(),
                amount,
                sourceAccount,
                destination
        );

        try {
            TransactionService.getInstance(storageMode)
                    .executeTransaction(tx);
            StyleUtils.showStyledSuccessDialog(this, "Transfer successful.");
            return true;
        } catch (Exception e) {
            StyleUtils.showStyledErrorDialog(this, e.getMessage());
            return false;
        }
    }
}
