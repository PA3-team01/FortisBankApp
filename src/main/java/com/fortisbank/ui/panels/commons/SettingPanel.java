package com.fortisbank.ui.panels.commons;

import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class SettingPanel extends JPanel {

    public SettingPanel() {
        setLayout(new GridBagLayout());
        StyleUtils.styleFormPanel(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // === Title ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE); // Texte blanc
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, gbc);

        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        // === Change Password Button ===
        gbc.gridy = 1;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 18));
        StyleUtils.styleButton(changePasswordButton, true);
        changePasswordButton.addActionListener(e -> openChangePasswordDialog());
        add(changePasswordButton, gbc);

        // === Update Email Button ===
        gbc.gridy = 2;
        JButton updateEmailButton = new JButton("Update Email");
        updateEmailButton.setFont(new Font("Arial", Font.BOLD, 18));
        StyleUtils.styleButton(updateEmailButton, true);
        updateEmailButton.addActionListener(e -> openUpdateEmailDialog());
        add(updateEmailButton, gbc);

        // === Update Phone Button ===
        gbc.gridy = 3;
        JButton updatePhoneButton = new JButton("Update Phone");
        updatePhoneButton.setFont(new Font("Arial", Font.BOLD, 18));
        StyleUtils.styleButton(updatePhoneButton, true);
        updatePhoneButton.addActionListener(e -> openUpdatePhoneDialog());
        add(updatePhoneButton, gbc);
    }

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
            } else {
                StyleUtils.showStyledErrorDialog(this, "Passwords do not match or are empty!");
            }
        }
    }

    private void openUpdateEmailDialog() {
        JTextField emailField = new JTextField();
        StyleUtils.styleTextField(emailField);

        Object[] message = {"Enter new email:", emailField};

        int option = JOptionPane.showConfirmDialog(this, message, "Update Email", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newEmail = emailField.getText().trim();

            if (!newEmail.isEmpty() && newEmail.contains("@")) {
                StyleUtils.showStyledSuccessDialog(this, "Email updated successfully!");
            } else {
                StyleUtils.showStyledErrorDialog(this, "Invalid email!");
            }
        }
    }

    private void openUpdatePhoneDialog() {
        JTextField phoneField = new JTextField();
        StyleUtils.styleTextField(phoneField);

        // Restreindre la saisie à uniquement des chiffres
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        Object[] message = {"Enter new phone number:", phoneField};

        int option = JOptionPane.showConfirmDialog(this, message, "Update Phone", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newPhone = phoneField.getText().trim();

            if (!newPhone.isEmpty() && newPhone.matches("\\d{10}")) {
                StyleUtils.showStyledSuccessDialog(this, "Phone number updated successfully!");
            } else {
                StyleUtils.showStyledErrorDialog(this, "Invalid phone number! Enter a 10-digit number.");
            }
        }
    }
}
