package com.fortisbank.ui.frames.mainFrames;

import com.fortisbank.business.services.customer.RegisterService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.uiUtils.StyleUtils;
import com.fortisbank.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField pinField = new JPasswordField(4);

    private final JLabel emailStatus = new JLabel(" ");
    private final JLabel phoneStatus = new JLabel(" ");
    private final JLabel passwordStatus = new JLabel(" ");
    private final JLabel pinStatus = new JLabel(" ");

    private final JButton registerButton = new JButton("Register");
    private final JButton cancelButton = new JButton("Cancel");

    private final JLabel statusLabel = new JLabel(" ");

    private final RegisterService registerService;

    public RegisterFrame(StorageMode storageMode) {
        this.registerService = RegisterService.getInstance(storageMode);

        // Custom undecorated window
        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Register New Customer", null);
        add(titleBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        setSize(500, 520);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:");
        contentPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(firstNameField, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:");
        contentPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(lastNameField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        contentPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(emailField, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        contentPanel.add(emailStatus, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel phoneLabel = new JLabel("Phone:");
        contentPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(phoneField, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        contentPanel.add(phoneStatus, gbc);

        // Row 6
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel passwordLabel = new JLabel("Password:");
        contentPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        contentPanel.add(passwordStatus, gbc);

        // Row 8
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel pinLabel = new JLabel("4-digit PIN:");
        contentPanel.add(pinLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(pinField, gbc);
        gbc.gridx = 1; gbc.gridy = 9;
        contentPanel.add(pinStatus, gbc);

        // Row 10 - Buttons
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 1;
        contentPanel.add(registerButton, gbc);
        gbc.gridx = 1;
        contentPanel.add(cancelButton, gbc);

        // Row 11 - Status
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(statusLabel, gbc);

        // Event actions
        registerButton.addActionListener(new RegisterAction());
        cancelButton.addActionListener(e -> dispose());

        // Apply Styles
        for (JLabel label : new JLabel[]{firstNameLabel, lastNameLabel, emailLabel, phoneLabel, passwordLabel, pinLabel, statusLabel}) {
            StyleUtils.styleLabel(label);
        }
        for (JLabel label : new JLabel[]{emailStatus, phoneStatus, passwordStatus, pinStatus}) {
            StyleUtils.styleStatusLabel(label, false);
        }
        for (JTextField field : new JTextField[]{firstNameField, lastNameField, emailField, phoneField}) {
            StyleUtils.styleTextField(field);
        }
        StyleUtils.stylePasswordField(passwordField);
        StyleUtils.stylePasswordField(pinField);
        StyleUtils.styleButton(registerButton, true);
        StyleUtils.styleButton(cancelButton, false);

        // Input restrictions and validation
        ValidationUtils.restrictToDigits(pinField, 4);
        ValidationUtils.attachRealTimeValidation(emailField, emailStatus, ValidationUtils::isValidEmail, "Valid email", "Invalid email");
        ValidationUtils.attachRealTimeValidation(phoneField, phoneStatus, ValidationUtils::isValidPhone, "Valid phone", "Invalid phone");
        ValidationUtils.attachRealTimeValidation(passwordField, passwordStatus, ValidationUtils::isStrongPassword, "Strong password", "Weak password");
        ValidationUtils.attachRealTimeValidation(pinField, pinStatus, ValidationUtils::isValidPIN, "Valid PIN", "4-digit PIN required");
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            char[] password = passwordField.getPassword();
            char[] pin = pinField.getPassword();

            try {
                boolean success = registerService.registerNewCustomer(firstName, lastName, email, phone, password, pin);
                if (success) {
                    StyleUtils.showStyledSuccessDialog(RegisterFrame.this,
                            "Registration successful! You may now log in.");
                    dispose();
                }
            } catch (Exception ex) {
                StyleUtils.showStyledErrorDialog(RegisterFrame.this, "Registration failed: " + ex.getMessage());
            } finally {
                passwordField.setText("");
                pinField.setText("");
            }
        }
    }

}
