package com.fortisbank.ui.panels.commons;

import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    public ProfilePanel() {
        setLayout(new GridBagLayout());
        StyleUtils.styleFormPanel(this);

        // Fetch current user info from SessionManager
        User currentUser = SessionManager.getCurrentUser();

        // === GridBag Constraints ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // === Profile Title ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("User Profile");
        StyleUtils.styleFormTitle(titleLabel);
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset width

        // === Labels for User Info ===
        addLabel("Full Name:", gbc, 1);
        addValue(currentUser.getFullName(), gbc, 1);

        addLabel("Email:", gbc, 2);
        addValue(currentUser.getEmail(), gbc, 2);


        addLabel("Role:", gbc, 3);
        addValue(currentUser.getRole().toString(), gbc, 3);


        addLabel("Phone Number:", gbc, 4);
        if (currentUser instanceof Customer customer) {
            addValue(customer.getPhoneNumber(), gbc, 4);
        } else {
            addValue("N/A", gbc, 4);
        }





        // Padding
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(Box.createVerticalStrut(20), gbc);
    }


    private void addLabel(String text, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(text);
        StyleUtils.styleLabel(label);
        add(label, gbc);
    }


    private void addValue(String text, GridBagConstraints gbc, int row) {
        gbc.gridx = 1;
        gbc.gridy = row;
        JLabel valueLabel = new JLabel(text);
        StyleUtils.styleLabel(valueLabel);
        add(valueLabel, gbc);
    }
}
