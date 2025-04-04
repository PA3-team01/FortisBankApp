package com.fortisbank.ui.frames.subFrames;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.ui.panels.commons.InboxPanel;
import com.fortisbank.ui.panels.commons.ProfilePanel;
import com.fortisbank.ui.panels.commons.SettingPanel;
import com.fortisbank.ui.panels.customerPanels.*;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class CustomerUi extends JPanel {

    private final NavigationBar navPanel;
    private final JPanel contentPanel;
    private StorageMode storageMode;

    public CustomerUi(StorageMode storageMode) {
        this.storageMode = storageMode;
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === LEFT: Navigation Panel ===
        navPanel = new NavigationBar("Inbox", "Accounts", "Transactions", "Currency Exchange", "Profile", "Contact", "Settings", "Help");
        StyleUtils.styleNavbar(navPanel);
        add(navPanel, BorderLayout.WEST);

        // === RIGHT: Content Area ===
        contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Show default welcome content
        showContent(new AccountPanel(storageMode));

        // === Navigation Hooks ===
        navPanel.setButtonAction("Inbox", () -> showContent(new InboxPanel(storageMode)));
        navPanel.setButtonAction("Accounts", () -> showContent(new AccountPanel(storageMode)));
        navPanel.setButtonAction("Transactions", () -> showContent(new TransactionPanel(storageMode)));
        navPanel.setButtonAction("Contact", () -> showContent(new SupportContactPanel(storageMode)));
        navPanel.setButtonAction("Settings", () -> showContent(new SettingPanel()));
        navPanel.setButtonAction("Currency Exchange", () -> showContent(new CurrencyExchangePanel(storageMode)));
        // Pass storageMode
        navPanel.setButtonAction("Help", () -> showContent(new HelpPanel(storageMode)));
        navPanel.setButtonAction("Profile", () -> showContent(new ProfilePanel()));

    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel info = new JLabel("Welcome! Select an option from the menu.");
        StyleUtils.styleLabel(info);
        panel.add(Box.createVerticalStrut(15));
        panel.add(info);

        return panel;
    }

    /**
     * Swap the right-side content dynamically with scroll support.
     */
    private void showContent(JComponent component) {
        contentPanel.removeAll();

        JLabel titleLabel = new JLabel("Customer Dashboard");
        StyleUtils.styleFormTitle(titleLabel);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(component);

        // Let child grow horizontally if possible
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
