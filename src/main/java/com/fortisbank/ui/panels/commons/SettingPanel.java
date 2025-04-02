package com.fortisbank.ui.panels.commons;

import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class SettingPanel extends JPanel {

    public SettingPanel() {
        setLayout(new GridBagLayout());
        StyleUtils.styleFormPanel(this);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Settings");
        StyleUtils.styleFormTitle(titleLabel);
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset width

        // === Change Password Option ===
        gbc.gridy = 1;
        addLabel("Change Password:", gbc, 1);
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> openChangePasswordDialog());
        add(changePasswordButton, gbc);

        // === Update Email Option ===
        gbc.gridy = 2;
        addLabel("Update Email:", gbc, 2);
        JButton updateEmailButton = new JButton("Update Email");
        updateEmailButton.addActionListener(e -> openUpdateEmailDialog());
        add(updateEmailButton, gbc);

        // === Update Phone Option ===
        gbc.gridy = 3;
        addLabel("Update Phone:", gbc, 3);
        JButton updatePhoneButton = new JButton("Update Phone");
        updatePhoneButton.addActionListener(e -> openUpdatePhoneDialog());
        add(updatePhoneButton, gbc);

        // Padding
        gbc.gridy = 4;
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

    // Placeholder methods for dialogs to update personal information
    private void openChangePasswordDialog() {
        JOptionPane.showMessageDialog(this, "Password Change");
    }

    private void openUpdateEmailDialog() {
        JOptionPane.showMessageDialog(this, "Email Update");
    }

    private void openUpdatePhoneDialog() {
        JOptionPane.showMessageDialog(this, "Phone Update");
    }
}
