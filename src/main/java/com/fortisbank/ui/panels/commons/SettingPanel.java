package com.fortisbank.ui.panels.commons;

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
        StyleUtils.styleButton(changePasswordButton, true);
        changePasswordButton.addActionListener(e -> openChangePasswordDialog());
        add(changePasswordButton, gbc);

        // === Update Email Option ===
        gbc.gridy = 2;
        addLabel("Update Email:", gbc, 2);
        JButton updateEmailButton = new JButton("Update Email");
        StyleUtils.styleButton(updateEmailButton, true);
        updateEmailButton.addActionListener(e -> openUpdateEmailDialog());
        add(updateEmailButton, gbc);

        // === Update Phone Option ===
        gbc.gridy = 3;
        addLabel("Update Phone:", gbc, 3);
        JButton updatePhoneButton = new JButton("Update Phone");
        StyleUtils.styleButton(updatePhoneButton, true);
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

    // Input Dialog for Changing Password
    private void openChangePasswordDialog() {
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        StyleUtils.stylePasswordField(newPasswordField);
        StyleUtils.stylePasswordField(confirmPasswordField);

        Object[] message = {
                "New Password:", newPasswordField,
                "Confirm Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.equals(confirmPassword) && !newPassword.isEmpty()) {
                StyleUtils.showStyledSuccessDialog(this, "Password updated successfully!");
                // Call your AccountService to update the password
            } else {
                StyleUtils.showStyledErrorDialog(this, "Passwords do not match or are empty!");
            }
        }
    }

    // Input Dialog for Updating Email
    private void openUpdateEmailDialog() {
        JTextField emailField = new JTextField();
        StyleUtils.styleTextField(emailField);

        Object[] message = {"Enter new email:", emailField};

        int option = JOptionPane.showConfirmDialog(this, message, "Update Email", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newEmail = emailField.getText().trim();

            if (!newEmail.isEmpty() && newEmail.contains("@")) {
                StyleUtils.showStyledSuccessDialog(this, "Email updated successfully!");
                //Call your AccountService to update the email
            } else {
                StyleUtils.showStyledErrorDialog(this, "Invalid email!");
            }
        }
    }

    // Input Dialog for Updating Phone Number
    private void openUpdatePhoneDialog() {
        JTextField phoneField = new JTextField();
        StyleUtils.styleTextField(phoneField);

        Object[] message = {"Enter new phone number:", phoneField};

        int option = JOptionPane.showConfirmDialog(this, message, "Update Phone", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newPhone = phoneField.getText().trim();

            if (!newPhone.isEmpty() && newPhone.matches("\\d{10}")) {
                StyleUtils.showStyledSuccessDialog(this, "Phone number updated successfully!");
                // CAll AccountService to update the phone number
            } else {
                StyleUtils.showStyledErrorDialog(this, "Invalid phone number! Enter a 10-digit number.");
            }
        }
    }
}
