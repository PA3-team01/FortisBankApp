package com.fortisbank.ui.forms;

import com.fortisbank.business.services.LoginService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.session.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardFrame extends JFrame {

    private final JLabel welcomeLabel = new JLabel();
    private final JButton logoutButton = new JButton("Logout");

    private final StorageMode storageMode;

    public DashboardFrame(StorageMode storageMode) {
        this.storageMode = storageMode;

        setTitle("Fortis Bank - Dashboard");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // Welcome label
        String name = SessionManager.getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Logout button
        logoutButton.addActionListener(this::handleLogout);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);

        add(welcomeLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogout(ActionEvent e) {
        SessionManager.clear();
        dispose();
        new LoginFrame(storageMode).setVisible(true);
    }
}
