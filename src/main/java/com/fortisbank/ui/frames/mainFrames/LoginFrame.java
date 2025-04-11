package com.fortisbank.ui.frames.mainFrames;

    import com.fortisbank.business.services.users.customer.CustomerService;
    import com.fortisbank.business.services.users.customer.LoginService;
    import com.fortisbank.business.services.users.manager.BankManagerService;
    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.contracts.models.users.User;
    import com.fortisbank.ui.ui_utils.StyleUtils;
    import com.fortisbank.contracts.utils.ValidationUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The LoginFrame class represents the login window of the Fortis Bank application.
     * It extends JFrame and provides a user interface for users to log in or register.
     */
    public class LoginFrame extends JFrame {

        private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());

        private final JTextField emailField = new JTextField(20);
        private final JPasswordField passwordField = new JPasswordField(20);
        private final JLabel emailStatus = new JLabel(" ");
        private final JLabel passwordStatus = new JLabel(" ");
        private final JButton loginButton = new JButton("Login");
        private final JButton registerButton = new JButton("New User? Register");
        private final JLabel statusLabel = new JLabel(" ");

        private final StorageMode storageMode;
        private final LoginService loginService;

        /**
         * Constructs a LoginFrame with the specified storage mode.
         *
         * @param storageMode the storage mode to use for services
         */
        public LoginFrame(StorageMode storageMode) {
            this.storageMode = storageMode;
            this.loginService = LoginService.getInstance(
                    CustomerService.getInstance(storageMode),
                    BankManagerService.getInstance(storageMode)
            );

            try {
                // Remove default OS title bar
                setUndecorated(true);
                setLayout(new BorderLayout());

                // Custom Title Bar
                JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Login", null);
                add(titleBar, BorderLayout.NORTH);

                // Main Content Panel
                JPanel contentPanel = new JPanel(new GridBagLayout());
                StyleUtils.styleFormPanel(contentPanel);
                add(contentPanel, BorderLayout.CENTER);

                setSize(600, 320);
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
                gbc.gridx = 1;
                gbc.gridy = 1;
                contentPanel.add(emailStatus, gbc);

                // Row 2 - Password
                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel passwordLabel = new JLabel("Password / PIN:");
                contentPanel.add(passwordLabel, gbc);
                gbc.gridx = 1;
                contentPanel.add(passwordField, gbc);
                gbc.gridx = 1;
                gbc.gridy = 3;
                contentPanel.add(passwordStatus, gbc);

                // Row 4 - Login button
                gbc.gridx = 0;
                gbc.gridy = 4;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                contentPanel.add(loginButton, gbc);

                // Row 5 - Register button
                gbc.gridy = 5;
                contentPanel.add(registerButton, gbc);

                // Row 6 - Status label
                gbc.gridy = 6;
                contentPanel.add(statusLabel, gbc);

                loginButton.addActionListener(new LoginAction());
                registerButton.addActionListener(new RegisterAction());
                getRootPane().setDefaultButton(loginButton);

                // Style components
                StyleUtils.styleLabel(emailLabel);
                StyleUtils.styleLabel(passwordLabel);
                StyleUtils.styleLabel(statusLabel);
                StyleUtils.styleStatusLabel(emailStatus, false);
                StyleUtils.styleStatusLabel(passwordStatus, false);
                StyleUtils.styleTextField(emailField);
                StyleUtils.stylePasswordField(passwordField);
                StyleUtils.styleButton(loginButton, true);
                StyleUtils.styleButton(registerButton, false);

                // Attach Real-Time Validation
                ValidationUtils.attachRealTimeValidation(emailField, emailStatus, ValidationUtils::isValidEmail, "Valid email", "Invalid email");
                ValidationUtils.attachRealTimeValidation(passwordField, passwordStatus, ValidationUtils::isStrongPassword, "Strong password", "Weak password");

                // TODO: remove this in production
                // Prefill the email field and password field
                emailField.setText("user@test.com");
                passwordField.setText("1111");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing LoginFrame: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the login frame: " + e.getMessage());
            }
        }

        /**
         * The LoginAction class handles the login button action.
         */
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
                        LOGGER.log(Level.WARNING, "PIN login failed for email: {0}", email);
                        user = loginService.loginWithPassword(email, passwordCopy);
                    }

                    statusLabel.setText("Welcome, " + user.getFullName());
                    dispose();
                    new DashboardFrame(storageMode).setVisible(true);

                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Login failed for email: {0}, Error: {1}", new Object[]{email, ex.getMessage()});
                    StyleUtils.showStyledErrorDialog(LoginFrame.this, "Login failed: " + ex.getMessage());
                } finally {
                    passwordField.setText("");
                }
            }
        }

        /**
         * The RegisterAction class handles the register button action.
         */
        private class RegisterAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new RegisterFrame(storageMode).setVisible(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error opening RegisterFrame: {0}", ex.getMessage());
                    StyleUtils.showStyledErrorDialog(LoginFrame.this, "Failed to open registration frame: " + ex.getMessage());
                }
            }
        }
    }