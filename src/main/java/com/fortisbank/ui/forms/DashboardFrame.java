package com.fortisbank.ui.forms;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.session.SessionManager;
import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardFrame extends JFrame {

    private final JLabel welcomeLabel = new JLabel();
    private final JButton logoutButton = new JButton("Logout");

    private final StorageMode storageMode;

    public DashboardFrame(StorageMode storageMode) {
        this.storageMode = storageMode;

        // Use custom undecorated frame
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Create custom title bar
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Dashboard");
        add(titleBar, BorderLayout.NORTH);

        // Welcome panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        String name = SessionManager.getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(StyleUtils.TEXT_COLOR);

        // Logout button
        StyleUtils.styleButton(logoutButton, false);
        logoutButton.addActionListener(this::handleLogout);

        JPanel buttonPanel = new JPanel();
        StyleUtils.styleFormPanel(buttonPanel);
        buttonPanel.add(logoutButton);

        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleLogout(ActionEvent e) {
        SessionManager.clear();
        dispose();
        new LoginFrame(storageMode).setVisible(true);
    }
}
