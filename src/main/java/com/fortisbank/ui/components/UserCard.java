package com.fortisbank.ui.components;

    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.contracts.models.users.BankManager;
    import com.fortisbank.contracts.models.users.Customer;
    import com.fortisbank.contracts.models.users.User;
    import com.fortisbank.ui.frames.subFrames.UserUpdateForm;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The UserCard class is a JPanel component that displays user information
     * and provides an edit button to update user details.
     */
    public class UserCard extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(UserCard.class.getName());

        /**
         * Constructs a UserCard for the given user and storage mode.
         *
         * @param user the user to display information for
         * @param storageMode the storage mode to use for services
         */
        public UserCard(User user, StorageMode storageMode) {
            try {
                setLayout(new BorderLayout());
                setBorder(BorderFactory.createLineBorder(StyleUtils.NAVBAR_BG));
                setBackground(StyleUtils.NAVBAR_BUTTON_COLOR);
                setOpaque(true);
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel leftPanel = createLeftPanel(user);
                JPanel rightPanel = createRightPanel(user, storageMode);

                add(leftPanel, BorderLayout.CENTER);
                add(rightPanel, BorderLayout.EAST);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing UserCard: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize user card: " + e.getMessage());
            }
        }

        /**
         * Creates the left panel containing user details.
         *
         * @param user the user to display information for
         * @return the left panel
         */
        private JPanel createLeftPanel(User user) {
            JPanel leftPanel = new JPanel();
            try {
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                leftPanel.setOpaque(false);

                String fullName = "Unknown User";
                if (user instanceof Customer c) {
                    fullName = c.getFullName();
                } else if (user instanceof BankManager m) {
                    fullName = m.getFullName();
                }

                JLabel nameLabel = new JLabel(fullName);
                JLabel emailLabel = new JLabel("Email: " + user.getEmail());
                JLabel roleLabel = new JLabel("Role: " + user.getRole().name());

                StyleUtils.styleLabel(nameLabel);
                StyleUtils.styleLabel(emailLabel);
                StyleUtils.styleLabel(roleLabel);

                leftPanel.add(nameLabel);
                leftPanel.add(emailLabel);
                leftPanel.add(roleLabel);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error creating left panel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to create user details panel: " + e.getMessage());
            }
            return leftPanel;
        }

        /**
         * Creates the right panel containing the edit button.
         *
         * @param user the user to edit
         * @param storageMode the storage mode to use for services
         * @return the right panel
         */
        private JPanel createRightPanel(User user, StorageMode storageMode) {
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            try {
                rightPanel.setOpaque(false);

                JButton editButton = new JButton("Edit");
                StyleUtils.styleButton(editButton, true);
                editButton.addActionListener(e -> {
                    try {
                        new UserUpdateForm(user, storageMode).setVisible(true);
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Error opening UserUpdateForm: {0}", ex.getMessage());
                        StyleUtils.showStyledErrorDialog(this, "Failed to open user update form: " + ex.getMessage());
                    }
                });

                rightPanel.add(editButton);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error creating right panel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to create edit button panel: " + e.getMessage());
            }
            return rightPanel;
        }
    }