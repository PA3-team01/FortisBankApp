package com.fortisbank.ui.panels;

import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ManagerUi extends JPanel {

    private final NavigationBar navPanel;
    private final JPanel contentPanel;

    public ManagerUi() {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === LEFT: Navigation Panel ===
        navPanel = new NavigationBar("Users", "Reports", "Approvals", "Settings");
        StyleUtils.styleNavbar(navPanel);
        add(navPanel, BorderLayout.WEST);

        // === RIGHT: Content Area ===
        contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);

        // Title
        JLabel titleLabel = new JLabel("Manager Dashboard");
        StyleUtils.styleFormTitle(titleLabel);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Placeholder content
        JPanel initialContent = new JPanel();
        initialContent.setLayout(new BoxLayout(initialContent, BoxLayout.Y_AXIS));
        initialContent.setOpaque(false);

        JLabel info = new JLabel("Welcome to the Manager Panel.");
        StyleUtils.styleLabel(info);

        initialContent.add(Box.createVerticalStrut(15));
        initialContent.add(info);

        contentPanel.add(initialContent, BorderLayout.CENTER);

        // Add content area to main layout
        add(contentPanel, BorderLayout.CENTER);

        // === Button actions ===
        navPanel.setButtonAction("Users", () -> showContent(new JLabel("Manage Users Section")));
        navPanel.setButtonAction("Reports", () -> showContent(new JLabel("Financial Reports")));
        navPanel.setButtonAction("Approvals", () -> showContent(new JLabel("Pending Approvals")));
        navPanel.setButtonAction("Settings", () -> showContent(new JLabel("System Settings")));
    }

    /**
     * Dynamically swap content panel contents.
     */
    private void showContent(JComponent component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
