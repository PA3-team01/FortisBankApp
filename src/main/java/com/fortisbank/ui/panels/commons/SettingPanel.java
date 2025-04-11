package com.fortisbank.ui.panels.commons;

    import com.fortisbank.ui.ui_utils.StyleUtils;
    import com.fortisbank.contracts.utils.ValidationUtils;

    import javax.swing.*;
    import javax.swing.text.AbstractDocument;
    import javax.swing.text.AttributeSet;
    import javax.swing.text.BadLocationException;
    import javax.swing.text.DocumentFilter;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The SettingPanel class represents the settings panel of the Fortis Bank application.
     * It extends JPanel and provides a user interface for updating user settings such as password, email, and phone number.
     */
    public class SettingPanel extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(SettingPanel.class.getName());

        /**
         * Constructs a SettingPanel and initializes the user interface components.
         */
        public SettingPanel() {
            try {
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
                titleLabel.setForeground(Color.WHITE); // White text
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

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing SettingPanel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the settings panel: " + e.getMessage());
            }
        }

        /**
         * Opens a dialog to change the user's password.
         */
        private void openChangePasswordDialog() {
            JPasswordField newPasswordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();
            JLabel passwordStatus = new JLabel(" ");
            StyleUtils.stylePasswordField(newPasswordField);
            StyleUtils.stylePasswordField(confirmPasswordField);
            StyleUtils.styleStatusLabel(passwordStatus, false);

            // Attach real-time validation for password strength
            ValidationUtils.attachRealTimeValidation(newPasswordField, passwordStatus, ValidationUtils::isStrongPassword, "Strong password", "Weak password");

            Object[] message = {
                    "New Password:", newPasswordField,
                    "Confirm Password:", confirmPasswordField,
                    passwordStatus
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

        /**
         * Opens a dialog to update the user's email.
         */
        private void openUpdateEmailDialog() {
            JTextField emailField = new JTextField();
            JLabel emailStatus = new JLabel(" ");
            StyleUtils.styleTextField(emailField);
            StyleUtils.styleStatusLabel(emailStatus, false);

            // Attach real-time validation for email format
            ValidationUtils.attachRealTimeValidation(emailField, emailStatus, ValidationUtils::isValidEmail, "Valid email", "Invalid email");

            Object[] message = {"Enter new email:", emailField, emailStatus};

            int option = JOptionPane.showConfirmDialog(this, message, "Update Email", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String newEmail = emailField.getText().trim();

                if (!newEmail.isEmpty() && ValidationUtils.isValidEmail(newEmail)) {
                    StyleUtils.showStyledSuccessDialog(this, "Email updated successfully!");
                } else {
                    StyleUtils.showStyledErrorDialog(this, "Invalid email!");
                }
            }
        }

        /**
         * Opens a dialog to update the user's phone number.
         */
        private void openUpdatePhoneDialog() {
            JTextField phoneField = new JTextField();
            JLabel phoneStatus = new JLabel(" ");
            StyleUtils.styleTextField(phoneField);
            StyleUtils.styleStatusLabel(phoneStatus, false);

            // Restrict input to digits only
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

            // Attach real-time validation for phone number format
            ValidationUtils.attachRealTimeValidation(phoneField, phoneStatus, text -> text.matches("\\d{10}"), "Valid phone number", "Invalid phone number");

            Object[] message = {"Enter new phone number:", phoneField, phoneStatus};

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