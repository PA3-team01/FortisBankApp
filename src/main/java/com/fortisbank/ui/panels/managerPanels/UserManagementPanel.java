package com.fortisbank.ui.panels.managerPanels;

    import com.fortisbank.business.services.users.customer.CustomerService;
    import com.fortisbank.business.services.users.manager.BankManagerService;
    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.contracts.collections.ManagerList;
    import com.fortisbank.contracts.models.users.Customer;
    import com.fortisbank.contracts.models.users.User;
    import com.fortisbank.ui.components.UserCard;
    import com.fortisbank.ui.ui_utils.StyleUtils;

    import javax.swing.*;
    import java.awt.*;
    import java.util.List;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * The UserManagementPanel class represents the user management panel of the Fortis Bank application.
     * It extends JPanel and provides a user interface to manage and filter users by role.
     */
    public class UserManagementPanel extends JPanel {

        private static final Logger LOGGER = Logger.getLogger(UserManagementPanel.class.getName());
        private final StorageMode storageMode;
        private final JPanel userListPanel = new JPanel();
        private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{"All", "Customer", "Manager"});

        /**
         * Constructs a UserManagementPanel with the specified storage mode.
         *
         * @param storageMode the storage mode to use for services
         */
        public UserManagementPanel(StorageMode storageMode) {
            this.storageMode = storageMode;
            try {
                setLayout(new BorderLayout());
                StyleUtils.styleFormPanel(this);

                JLabel title = new JLabel("User Management");
                StyleUtils.styleFormTitle(title);
                add(title, BorderLayout.NORTH);

                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                topPanel.setOpaque(false);

                JLabel filterLabel = new JLabel("Filter by Role:");
                StyleUtils.styleLabel(filterLabel);
                StyleUtils.styleDropdown(roleFilter);
                roleFilter.addActionListener(e -> refreshUsers());

                topPanel.add(filterLabel);
                topPanel.add(roleFilter);
                add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

                userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
                userListPanel.setOpaque(false);

                JScrollPane scrollPane = new JScrollPane(userListPanel);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setBorder(null);
                add(scrollPane, BorderLayout.CENTER);

                refreshUsers();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing UserManagementPanel: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to initialize the user management panel: " + e.getMessage());
            }
        }

        /**
         * Refreshes the user list based on the selected role filter.
         */
        private void refreshUsers() {
            try {
                userListPanel.removeAll();
                String selectedRole = roleFilter.getSelectedItem().toString();

                if (selectedRole.equals("All") || selectedRole.equals("Customer")) {
                    List<Customer> customers = CustomerService.getInstance(storageMode).getAllCustomers();
                    for (Customer customer : customers) {
                        userListPanel.add(new UserCard(customer, storageMode));
                        userListPanel.add(Box.createVerticalStrut(10));
                    }
                }

                if (selectedRole.equals("All") || selectedRole.equals("Manager")) {
                    ManagerList managers = BankManagerService.getInstance(storageMode).getAllManagers();
                    for (User manager : managers) {
                        userListPanel.add(new UserCard(manager, storageMode));
                        userListPanel.add(Box.createVerticalStrut(10));
                    }
                }

                userListPanel.revalidate();
                userListPanel.repaint();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error refreshing user list: {0}", e.getMessage());
                StyleUtils.showStyledErrorDialog(this, "Failed to refresh the user list: " + e.getMessage());
            }
        }
    }