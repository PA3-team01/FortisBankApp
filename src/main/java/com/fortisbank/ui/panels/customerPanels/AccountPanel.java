package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.users.Customer;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.components.AccountInfo;
import com.fortisbank.ui.forms.AccountRequestForm;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {

    private final StorageMode storageMode;

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

        AccountList accounts = customer.getAccounts();

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
