package com.fortisbank.ui.forms;

import com.fortisbank.business.services.CustomerService;
import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.accounts.AccountType;
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

    public TransferForm(Account sourceAccount, StorageMode storageMode) {
        super("Transfer Funds", storageMode);
        this.sourceAccount = sourceAccount;

        setupCustomerDropdown();
        buildTransferPanel();
    }

    private void setupCustomerDropdown() {
        CustomerList customers = CustomerService.getInstance(storageMode).getAllCustomers();

        for (Customer customer : customers) {
            customerSelector.addItem(customer);
        }

        customerSelector.addActionListener(e -> {
            Customer selected = (Customer) customerSelector.getSelectedItem();
            destinationSelector.removeAllItems();

            if (selected != null) {
                for (Account acc : selected.getAccounts()) {
                    if (acc.getAccountType() == AccountType.CHECKING && acc.isActive()) {
                        destinationSelector.addItem(acc);
                    }
                }
            }
        });

        StyleUtils.styleDropdown(customerSelector);
        StyleUtils.styleDropdown(destinationSelector);
    }

    private void buildTransferPanel() {
        JPanel transferPanel = new JPanel();
        transferPanel.setLayout(new BoxLayout(transferPanel, BoxLayout.Y_AXIS));
        transferPanel.setOpaque(false);

        JLabel customerLabel = new JLabel("Select Recipient:");
        JLabel accountLabel = new JLabel("Select Account:");
        StyleUtils.styleLabel(customerLabel);
        StyleUtils.styleLabel(accountLabel);

        transferPanel.add(customerLabel);
        transferPanel.add(customerSelector);
        transferPanel.add(Box.createVerticalStrut(10));
        transferPanel.add(accountLabel);
        transferPanel.add(destinationSelector);

        getContentPane().add(transferPanel, BorderLayout.WEST);
    }

    @Override
    protected boolean handleConfirm() {
        Account destination = (Account) destinationSelector.getSelectedItem();

        if (destination == null) {
            StyleUtils.showStyledErrorDialog(this, "Please select a destination account.");
            return false;
        }

        BigDecimal amount = getEnteredAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
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
