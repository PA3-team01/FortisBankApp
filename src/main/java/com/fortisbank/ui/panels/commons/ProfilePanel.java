package com.fortisbank.ui.panels.commons;

    import com.fortisbank.contracts.models.users.Customer;
    import com.fortisbank.contracts.models.users.User;
    import com.fortisbank.business.services.session.SessionManager;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The ProfilePanel class represents the profile panel of the Fortis Bank application.
     * It extends JPanel and provides a user interface to display the current user's profile information.
     */
    public class ProfilePanel extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(ProfilePanel.class.getName());

        /**
         * Constructs a ProfilePanel and initializes the user interface components.
         */
        public ProfilePanel() {
            try {
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

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing ProfilePanel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the profile panel: " + e.getMessage());
            }
        }

        /**
         * Adds a label to the panel at the specified row.
         *
         * @param text the text of the label
         * @param gbc the GridBagConstraints to use for layout
         * @param row the row at which to add the label
         */
        private void addLabel(String text, GridBagConstraints gbc, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            JLabel label = new JLabel(text);
            StyleUtils.styleLabel(label);
            add(label, gbc);
        }

        /**
         * Adds a value label to the panel at the specified row.
         *
         * @param text the text of the value label
         * @param gbc the GridBagConstraints to use for layout
         * @param row the row at which to add the value label
         */
        private void addValue(String text, GridBagConstraints gbc, int row) {
            gbc.gridx = 1;
            gbc.gridy = row;
            JLabel valueLabel = new JLabel(text);
            StyleUtils.styleLabel(valueLabel);
            add(valueLabel, gbc);
        }
    }