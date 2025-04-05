package com.fortisbank.ui.frames.subFrames;

import com.fortisbank.business.services.customer.CustomerService;
import com.fortisbank.business.services.manager.BankManagerService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;
import com.fortisbank.ui.uiUtils.StyleUtils;
import com.fortisbank.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserUpdateForm extends JFrame {

    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JPasswordField newPasswordField = new JPasswordField(20);
    private final JPasswordField newPinField = new JPasswordField(4);

    private final JLabel emailStatus = new JLabel(" ");
    private final JLabel phoneStatus = new JLabel(" ");
    private final JLabel passwordStatus = new JLabel(" ");
    private final JLabel pinStatus = new JLabel(" ");

    private final JButton updateButton = new JButton("Update User");
    private final JButton cancelButton = new JButton("Cancel");

    private final JLabel statusLabel = new JLabel(" ");

    private final StorageMode storageMode;
    private final User user;

    public UserUpdateForm(User user, StorageMode storageMode) {
        this.user = user;
        this.storageMode = storageMode;

        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Edit User Information", null);
        add(titleBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        setSize(500, 520);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:");
        contentPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:");
        contentPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        contentPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(emailField, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        contentPanel.add(emailStatus, gbc);

        int row = 4;
        boolean isCustomer = user instanceof Customer;
        if (isCustomer) {
            gbc.gridx = 0; gbc.gridy = row;
            JLabel phoneLabel = new JLabel("Phone:");
            contentPanel.add(phoneLabel, gbc);
            gbc.gridx = 1;
            contentPanel.add(phoneField, gbc);
            gbc.gridx = 1; gbc.gridy = ++row;
            contentPanel.add(phoneStatus, gbc);
        }

        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel passwordLabel = new JLabel("New Password (optional):");
        contentPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(newPasswordField, gbc);
        gbc.gridx = 1; gbc.gridy = ++row;
        contentPanel.add(passwordStatus, gbc);

        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel pinLabel = new JLabel("New 4-digit PIN (optional):");
        contentPanel.add(pinLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(newPinField, gbc);
        gbc.gridx = 1; gbc.gridy = ++row;
        contentPanel.add(pinStatus, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 1;
        contentPanel.add(updateButton, gbc);
        gbc.gridx = 1;
        contentPanel.add(cancelButton, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(statusLabel, gbc);

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        if (isCustomer) {
            phoneField.setText(((Customer) user).getPhoneNumber());
        }

        updateButton.addActionListener(new UpdateAction());
        cancelButton.addActionListener(e -> dispose());

        for (JLabel label : new JLabel[]{firstNameLabel, lastNameLabel, emailLabel, passwordLabel, pinLabel, statusLabel}) {
            StyleUtils.styleLabel(label);
        }
        if (isCustomer) {
            StyleUtils.styleLabel(phoneStatus);
            StyleUtils.styleTextField(phoneField);
        }
        for (JLabel label : new JLabel[]{emailStatus, passwordStatus, pinStatus}) {
            StyleUtils.styleStatusLabel(label, false);
        }
        for (JTextField field : new JTextField[]{firstNameField, lastNameField, emailField}) {
            StyleUtils.styleTextField(field);
        }
        StyleUtils.stylePasswordField(newPasswordField);
        StyleUtils.stylePasswordField(newPinField);
        StyleUtils.styleButton(updateButton, true);
        StyleUtils.styleButton(cancelButton, false);

        ValidationUtils.restrictToDigits(newPinField, 4);
        ValidationUtils.attachRealTimeValidation(emailField, emailStatus, ValidationUtils::isValidEmail, "Valid email", "Invalid email");
        if (isCustomer) {
            ValidationUtils.attachRealTimeValidation(phoneField, phoneStatus, ValidationUtils::isValidPhone, "Valid phone", "Invalid phone");
        }
        ValidationUtils.attachRealTimeValidation(newPasswordField, passwordStatus, ValidationUtils::isStrongPassword, "Strong password", "Weak password");
        ValidationUtils.attachRealTimeValidation(newPinField, pinStatus, ValidationUtils::isValidPIN, "Valid PIN", "4-digit PIN required");
    }

    private class UpdateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                user.setFirstName(firstNameField.getText().trim());
                user.setLastName(lastNameField.getText().trim());
                user.setEmail(emailField.getText().trim());

                if (user instanceof Customer customer) {
                    customer.setPhoneNumber(phoneField.getText().trim());
                }

                char[] newPassword = newPasswordField.getPassword();
                char[] newPin = newPinField.getPassword();

                if (user instanceof Customer customer) {
                    CustomerService.getInstance(storageMode).updateCustomerWithSecurity(customer, newPassword, newPin);
                } else if (user instanceof BankManager manager) {
                    BankManagerService.getInstance(storageMode).updateManagerWithSecurity(manager, newPassword, newPin);
                }

                StyleUtils.showStyledSuccessDialog(UserUpdateForm.this, "User updated successfully.");
                dispose();
            } catch (Exception ex) {
                StyleUtils.showStyledErrorDialog(UserUpdateForm.this, "Update failed: " + ex.getMessage());
            }
        }
    }
}