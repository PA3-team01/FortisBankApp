package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.collections.AccountList;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.business.services.session.SessionManager;
import com.fortisbank.ui.components.AccountInfo;
import com.fortisbank.ui.forms.AccountRequestForm;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

/**
 * The AccountPanel class represents the account panel of the Fortis Bank application.
 * It extends JPanel and provides a user interface to display the customer's active accounts
 * and allows the customer to request a new account.
 */
public class AccountPanel extends JPanel {

    private final StorageMode storageMode;

    /**
     * Constructs an AccountPanel with the specified storage mode.
     *
     * @param storageMode the storage mode to use for services
     */
    public AccountPanel(StorageMode storageMode) {
        this.storageMode = storageMode;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        StyleUtils.styleFormPanel(this);

        Customer customer = SessionManager.getCustomer();
        if (customer == null) {
            JLabel error = new JLabel("Error: No customer session found.");
            StyleUtils.styleLabel(error);
            add(error);
            return;
        }

        // Get the customer's accounts
        AccountList accounts = customer.getAccounts();
        // Filter out inactive accounts
        accounts = accounts.stream()
                .filter(Account::isActive)
                .collect(AccountList::new, AccountList::add, AccountList::addAll);

        if (accounts == null || accounts.isEmpty()) {
            JLabel info = new JLabel("You currently have no accounts.");
            StyleUtils.styleLabel(info);
            add(info);
        } else {
            for (Account account : accounts) {
                AccountInfo infoCard = new AccountInfo(account, storageMode);
                add(Box.createVerticalStrut(15));
                add(infoCard);
            }
        }

        JButton requestBtn = new JButton("Request New Account");
        StyleUtils.styleButton(requestBtn, true);
        requestBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        requestBtn.addActionListener(e -> {
            new AccountRequestForm(storageMode).setVisible(true);
        });

        add(Box.createVerticalStrut(20));
        add(requestBtn);
        add(Box.createVerticalGlue());
    }
}