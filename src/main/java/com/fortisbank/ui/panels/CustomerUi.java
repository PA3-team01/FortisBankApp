package com.fortisbank.ui.panels;

import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class CustomerUi extends JPanel {

    private final NavigationBar navPanel;
    private final JPanel contentPanel;

    public CustomerUi() {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === LEFT: Navigation Panel ===
        navPanel = new NavigationBar("Accounts", "Transactions", "Settings", "Help");
        StyleUtils.styleNavbar(navPanel);
        add(navPanel, BorderLayout.WEST);

        // === RIGHT: Content Area ===
        contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);

        // Placeholder title
        JLabel titleLabel = new JLabel("Customer Dashboard");
        StyleUtils.styleFormTitle(titleLabel);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Initial placeholder content
        JPanel initialContent = new JPanel();
        initialContent.setLayout(new BoxLayout(initialContent, BoxLayout.Y_AXIS));
        initialContent.setOpaque(false);

        JLabel info = new JLabel("Welcome! Select an option from the menu.");
        StyleUtils.styleLabel(info);
        initialContent.add(Box.createVerticalStrut(15));
        initialContent.add(info);

        contentPanel.add(initialContent, BorderLayout.CENTER);

        // === Add content panel to main layout ===
        add(contentPanel, BorderLayout.CENTER);

        // === Example button hook ===
        navPanel.setButtonAction("Accounts", () -> showContent(new JLabel("Accounts Section")));
        navPanel.setButtonAction("Transactions", () -> showContent(new JLabel("Transactions Section")));
        navPanel.setButtonAction("Settings", () -> showContent(new JLabel("Settings")));
        navPanel.setButtonAction("Help", () -> showContent(new JLabel("Help & Support")));
    }

    /**
     * Swap the right-side content dynamically.
     */
    private void showContent(JComponent component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
