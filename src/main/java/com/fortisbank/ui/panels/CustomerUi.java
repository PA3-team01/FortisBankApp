package com.fortisbank.ui.panels;

    import com.fortisbank.data.dal_utils.StorageMode;
    import com.fortisbank.ui.components.NavigationBar;
    import com.fortisbank.ui.panels.commons.*;
    import com.fortisbank.ui.panels.customerPanels.*;

    import javax.swing.*;

    /**
     * The CustomerUi class represents the user interface for customers.
     */
    public class CustomerUi extends UserUI {

        public CustomerUi(StorageMode storageMode) {
            super(storageMode);
        }

        @Override
        protected NavigationBar createNavigationBar() {
            return new NavigationBar("Inbox", "Accounts", "Transactions", "Currency Exchange", "Profile", "Contact", "Settings", "Help");
        }

        @Override
        protected void setupNavigationActions() {
            navPanel.setButtonAction("Inbox", () -> showContent(new InboxPanel(storageMode)));
            navPanel.setButtonAction("Accounts", () -> showContent(new AccountPanel(storageMode)));
            navPanel.setButtonAction("Transactions", () -> showContent(new TransactionPanel(storageMode)));
            navPanel.setButtonAction("Contact", () -> showContent(new SupportContactPanel(storageMode)));
            navPanel.setButtonAction("Settings", () -> showContent(new SettingPanel()));
            navPanel.setButtonAction("Currency Exchange", () -> showContent(new CurrencyExchangePanel(storageMode)));
            navPanel.setButtonAction("Help", () -> showContent(new HelpPanel(storageMode)));
            navPanel.setButtonAction("Profile", () -> showContent(new ProfilePanel()));
        }

        @Override
        protected JPanel createWelcomePanel() {
            JPanel panel = new JPanel();
            panel.add(new JLabel("Welcome to the Customer Dashboard!"));
            return panel;
        }

        @Override
        protected String getDashboardTitle() {
            return "Customer Dashboard";
        }
    }