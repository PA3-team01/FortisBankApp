package com.fortisbank.ui.forms;

import com.fortisbank.business.services.TransactionService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.transactions.Transaction;
import com.fortisbank.models.transactions.TransactionFactory;
import com.fortisbank.models.transactions.TransactionType;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

public class TransferForm extends TransactionForm {

    private final Account sourceAccount;
    private final JComboBox<Account> destinationSelector = new JComboBox<>();

    public TransferForm(Account sourceAccount, StorageMode storageMode) {
        super("Transfer Funds", storageMode);
        this.sourceAccount = sourceAccount;

        setupDestinationDropdown();
        buildDestinationPanel();
    }

    private void setupDestinationDropdown() {
        for (Account acc : SessionManager.getCustomer().getAccounts()) {
            if (!acc.getAccountNumber().equals(sourceAccount.getAccountNumber())) {
                destinationSelector.addItem(acc);
            }
        }

        StyleUtils.styleDropdown(destinationSelector);
    }

    private void buildDestinationPanel() {
        JPanel destPanel = new JPanel(new BorderLayout());
        destPanel.setOpaque(false);

        JLabel destLabel = new JLabel("Destination Account:");
        StyleUtils.styleLabel(destLabel);

        destPanel.add(destLabel, BorderLayout.NORTH);
        destPanel.add(destinationSelector, BorderLayout.CENTER);

        getContentPane().add(destPanel, BorderLayout.WEST);
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
