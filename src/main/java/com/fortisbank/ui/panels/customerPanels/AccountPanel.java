package com.fortisbank.ui.panels.customerPanels;

import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.collections.AccountList;
import com.fortisbank.models.users.Customer;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.components.AccountInfo;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

//TODO: implement request new account/loan
public class AccountPanel extends JPanel {

    public AccountPanel() {
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
                AccountInfo infoCard = new AccountInfo(account); // Now uses Account object
                add(Box.createVerticalStrut(15));
                add(infoCard);
            }
        }

        add(Box.createVerticalGlue());
    }
}
