package com.fortisbank.ui.forms;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.Role;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.components.NavigationPanel;
import com.fortisbank.ui.panels.CustomerUi;
import com.fortisbank.ui.panels.ManagerUi;
import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardFrame extends JFrame {

    private final StorageMode storageMode;

    public DashboardFrame(StorageMode storageMode) {
        this.storageMode = storageMode;

        // Undecorated window with custom title bar
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Title bar
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Dashboard");
        add(titleBar, BorderLayout.NORTH);

        // Navigation panel with Logout
        NavigationPanel navPanel = new NavigationPanel("Logout");
        navPanel.setButtonAction("Logout", this::handleLogout);
        StyleUtils.styleFormPanel(navPanel);
        add(navPanel, BorderLayout.WEST);

        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Welcome label
        String name = SessionManager.getCurrentUser().getFullName();
        JLabel welcomeLabel = new JLabel("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(StyleUtils.TEXT_COLOR);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Role-specific panel
        JPanel rolePanel;
        Role role = SessionManager.getCurrentUser().getRole();
        switch (role) {
            case MANAGER -> rolePanel = new ManagerUi();
            case CUSTOMER -> rolePanel = new CustomerUi();
            default -> rolePanel = new JPanel(); // fallback if unknown role
        }
        StyleUtils.styleFormPanel(rolePanel);
        contentPanel.add(rolePanel, BorderLayout.CENTER);

        // Final frame setup
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleLogout() {
        SessionManager.clear();
        dispose();
        new LoginFrame(storageMode).setVisible(true);
    }
}
