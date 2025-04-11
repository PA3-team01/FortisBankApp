package com.fortisbank.ui.panels;

    import com.fortisbank.business.services.users.customer.CustomerService;
    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.ui.components.NavigationBar;
    import com.fortisbank.ui.panels.commons.*;
    import com.fortisbank.ui.panels.managerPanels.*;

    import javax.swing.*;

    /**
     * The ManagerUi class represents the user interface for managers.
     */
    public class ManagerUi extends UserUI {

        public ManagerUi(StorageMode storageMode) {
            super(storageMode);
        }

        @Override
        protected NavigationBar createNavigationBar() {
            return new NavigationBar("Inbox", "Users", "Reports", "Interest Rates", "Settings");
        }

        @Override
        protected void setupNavigationActions() {
            navPanel.setButtonAction("Inbox", () -> showContent(new InboxPanel(storageMode)));
            navPanel.setButtonAction("Users", () -> showContent(new UserManagementPanel(storageMode)));
            navPanel.setButtonAction("Reports", () -> showContent(new ReportsPanel(storageMode,
                    CustomerService.getInstance(storageMode).getAllCustomers())));
            navPanel.setButtonAction("Interest Rates", () -> showContent(new InterestRateManager()));
            navPanel.setButtonAction("Settings", () -> showContent(new SettingPanel()));
        }

        @Override
        protected JPanel createWelcomePanel() {
            JPanel panel = new JPanel();
            panel.add(new JLabel("Welcome to the Manager Dashboard!"));
            return panel;
        }

        @Override
        protected String getDashboardTitle() {
            return "Manager Dashboard";
        }
    }