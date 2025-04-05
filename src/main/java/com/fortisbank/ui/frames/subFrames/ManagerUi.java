package com.fortisbank.ui.frames.subFrames;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.ui.panels.commons.InboxPanel;
import com.fortisbank.ui.panels.commons.ProfilePanel;
import com.fortisbank.ui.panels.commons.SettingPanel;
import com.fortisbank.ui.panels.managerPanels.InterestRateManager;
import com.fortisbank.ui.panels.managerPanels.UserManagementPanel;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ManagerUi extends JPanel {

    private final NavigationBar navPanel;
    private final JPanel contentPanel;
    private StorageMode storageMode;

    public ManagerUi(StorageMode storageMode) {
        this.storageMode = storageMode;
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === LEFT: Navigation Panel ===
        navPanel = new NavigationBar("Inbox", "Users", "Reports", "Interest Rates", "Settings");
        StyleUtils.styleNavbar(navPanel);
        add(navPanel, BorderLayout.WEST);

        // === RIGHT: Content Area ===
        contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Show default welcome content
        showContent(createWelcomePanel());

        // === Button Actions ===
        navPanel.setButtonAction("Inbox", () -> showContent(new InboxPanel(storageMode)));
        navPanel.setButtonAction("Users", () -> showContent(new UserManagementPanel(storageMode)));
        navPanel.setButtonAction("Reports", () -> showContent(new JLabel("Financial Reports")));
        navPanel.setButtonAction("Interest Rates", () -> showContent(new InterestRateManager()));
        //navPanel.setButtonAction("Settings", () -> showContent(new JLabel("System Settings")));
        navPanel.setButtonAction("Settings", () -> showContent(new SettingPanel()));

        navPanel.setButtonAction("Profile", () -> showContent(new ProfilePanel()));

    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel info = new JLabel("Welcome to the Manager Panel.");
        StyleUtils.styleLabel(info);
        panel.add(Box.createVerticalStrut(15));
        panel.add(info);

        return panel;
    }

    /**
     * Dynamically swap content panel contents.
     */
    private void showContent(JComponent component) {
        contentPanel.removeAll();

        JLabel titleLabel = new JLabel("Manager Dashboard");
        StyleUtils.styleFormTitle(titleLabel);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(component);

        // Allow component to expand horizontally if possible
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scroll
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
