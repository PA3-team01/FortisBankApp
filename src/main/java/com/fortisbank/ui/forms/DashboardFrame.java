package com.fortisbank.ui.forms;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.Role;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.panels.CustomerUi;
import com.fortisbank.ui.panels.ManagerUi;
import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final StorageMode storageMode;

    public DashboardFrame(StorageMode storageMode) {
        this.storageMode = storageMode;

        // Undecorated window with custom title bar
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Top-right logout button
        JButton logoutButton = new JButton("Logout");
        StyleUtils.styleButton(logoutButton, false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightControls.setOpaque(false);
        rightControls.add(logoutButton);

        // Title bar with logout on top right
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Dashboard", rightControls);
        add(titleBar, BorderLayout.NORTH);

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
            default -> rolePanel = new JPanel(); // fallback
        }
        StyleUtils.styleFormPanel(rolePanel);
        contentPanel.add(rolePanel, BorderLayout.CENTER);

        // Frame setup
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
