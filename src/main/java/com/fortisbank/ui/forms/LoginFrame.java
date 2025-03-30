package com.fortisbank.ui.forms;

import com.fortisbank.business.services.BankManagerService;
import com.fortisbank.business.services.CustomerService;
import com.fortisbank.business.services.LoginService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JLabel statusLabel = new JLabel(" ");

    private final StorageMode storageMode;
    private final LoginService loginService;

    public LoginFrame(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.loginService = LoginService.getInstance(
                CustomerService.getInstance(storageMode),
                BankManagerService.getInstance(storageMode)
        );

        setTitle("Fortis Bank - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Center the window

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 - Email label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        // Row 1 - Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password / PIN:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        // Row 2 - Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Row 3 - Status label
        gbc.gridy = 3;
        add(statusLabel, gbc);

        loginButton.addActionListener(new LoginAction());
        getRootPane().setDefaultButton(loginButton);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            char[] input = passwordField.getPassword();
            char[] passwordCopy = input.clone();  // Save a copy before PIN clears it

            try {
                User user;
                try {
                    user = loginService.loginWithPIN(email, input);
                } catch (Exception pinFail) {
                    user = loginService.loginWithPassword(email, passwordCopy);
                }

                statusLabel.setText("Welcome, " + user.getFullName());
                dispose();
                new DashboardFrame(storageMode).setVisible(true); // Open the dashboard frame

            } catch (Exception ex) {
                statusLabel.setText("Login failed: " + ex.getMessage());
            } finally {
                passwordField.setText("");
            }
        }
    }
}
