package com.fortisbank.ui.frames.mainFrames;

import com.fortisbank.business.services.customer.CustomerService;
import com.fortisbank.business.services.customer.LoginService;
import com.fortisbank.business.services.manager.BankManagerService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.User;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JButton registerButton = new JButton("New User? Register");
    private final JLabel statusLabel = new JLabel(" ");

    private final StorageMode storageMode;
    private final LoginService loginService;

    public LoginFrame(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.loginService = LoginService.getInstance(
                CustomerService.getInstance(storageMode),
                BankManagerService.getInstance(storageMode)
        );

        // Remove default OS title bar
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Custom Title Bar
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Login",null);
        add(titleBar, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        setSize(600, 280);
        setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 - Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        contentPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(emailField, gbc);

        // Row 1 - Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password / PIN:");
        contentPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        // Row 2 - Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(loginButton, gbc);

        // Row 3 - Register button
        gbc.gridy = 3;
        contentPanel.add(registerButton, gbc);

        // Row 4 - Status label
        gbc.gridy = 4;
        contentPanel.add(statusLabel, gbc);

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());
        getRootPane().setDefaultButton(loginButton);

        // Style components
        StyleUtils.styleLabel(emailLabel);
        StyleUtils.styleLabel(passwordLabel);
        StyleUtils.styleLabel(statusLabel);
        StyleUtils.styleTextField(emailField);
        StyleUtils.styleTextField(passwordField);
        StyleUtils.styleButton(loginButton, true);
        StyleUtils.styleButton(registerButton, false);

        //TODO: remove this in production
        // prefill the email field and password field
        emailField.setText("user@test.com");
        passwordField.setText("1111");
    }


    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            char[] input = passwordField.getPassword();
            char[] passwordCopy = input.clone();

            try {
                User user;
                try {
                    user = loginService.loginWithPIN(email, input);
                } catch (Exception pinFail) {
                    user = loginService.loginWithPassword(email, passwordCopy);
                }

                statusLabel.setText("Welcome, " + user.getFullName());
                dispose();
                new DashboardFrame(storageMode).setVisible(true);

            } catch (Exception ex) {
                statusLabel.setText("Login failed: " + ex.getMessage());
            } finally {
                passwordField.setText("");
            }
        }
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new RegisterFrame(storageMode).setVisible(true);
        }
    }
}
