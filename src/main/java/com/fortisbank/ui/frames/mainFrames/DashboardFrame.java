package com.fortisbank.ui.frames.mainFrames;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.users.Role;
import com.fortisbank.business.services.session.SessionManager;
import com.fortisbank.ui.panels.CustomerUi;
import com.fortisbank.ui.panels.ManagerUi;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The DashboardFrame class represents the main dashboard window of the Fortis Bank application.
 * It extends JFrame and provides a user interface based on the user's role (Manager or Customer).
 */
public class DashboardFrame extends JFrame {

    private final StorageMode storageMode;

    /**
     * Constructs a DashboardFrame with the specified storage mode.
     *
     * @param storageMode the storage mode to use for services
     */
    public DashboardFrame(StorageMode storageMode) {
        this.storageMode = storageMode;

        // === Frame setup ===
        setUndecorated(true);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setMinimumSize(new Dimension(1000, 600)); // Prevent tiny shrinking
        setResizable(true); // Allow resizing

        // === Add Window Listener to Close Open Forms ===
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAllTransactionForms();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                closeAllTransactionForms();
            }
        });

        // === Top-right Logout Button ===
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

        // === Custom Title Bar ===
        JPanel titleBar = StyleUtils.createCustomTitleBar(this, "Fortis Bank - Dashboard", rightControls);
        add(titleBar, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        // === Welcome Label ===
        String name = SessionManager.getCurrentUser().getFullName();
        JLabel welcomeLabel = new JLabel("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(StyleUtils.TEXT_COLOR);
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        // === Role-Specific Panel ===
        JPanel rolePanel;
        Role role = SessionManager.getCurrentUser().getRole();
        switch (role) {
            case MANAGER -> rolePanel = new ManagerUi(storageMode);
            case CUSTOMER -> rolePanel = new CustomerUi(storageMode);
            default -> rolePanel = new JPanel(); // fallback
        }

        StyleUtils.styleFormPanel(rolePanel);
        contentPanel.add(rolePanel, BorderLayout.CENTER);
    }

    /**
     * Handles the logout action by clearing the session and opening the login frame.
     */
    private void handleLogout() {
        SessionManager.clear();

        // Open login frame AFTER dashboard is disposed to avoid conflicts
        SwingUtilities.invokeLater(() -> new LoginFrame(storageMode).setVisible(true));

        // Dispose of dashboard after scheduling login
        dispose();
    }

    /**
     * Closes all open transaction forms when the dashboard is closed.
     */
    private void closeAllTransactionForms() {
        for (Window window : Window.getWindows()) {
            if ((window instanceof JDialog || window instanceof JFrame)
                    && window != this
                    && !(window instanceof LoginFrame)) { // Don't close the new LoginFrame
                window.dispose();
            }
        }
    }
}