package com.fortisbank.ui.panels;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for user interfaces (e.g., CustomerUi, ManagerUi).
 * Provides common functionality for navigation and content display.
 */
public abstract class UserUI extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(UserUI.class.getName());
    protected final NavigationBar navPanel;
    protected final JPanel contentPanel;
    protected final StorageMode storageMode;

    /**
     * Constructs a UserUI with the specified storage mode.
     *
     * @param storageMode the storage mode to use for services
     */
    public UserUI(StorageMode storageMode) {
        this.storageMode = storageMode;
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // === LEFT: Navigation Panel ===
        navPanel = createNavigationBar();
        StyleUtils.styleNavbar(navPanel);
        add(navPanel, BorderLayout.WEST);

        // === RIGHT: Content Area ===
        contentPanel = new JPanel(new BorderLayout());
        StyleUtils.styleFormPanel(contentPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Show default welcome content
        showContent(createWelcomePanel());

        // Setup navigation actions
        setupNavigationActions();
    }

    /**
     * Creates the navigation bar for the UI.
     *
     * @return the navigation bar
     */
    protected abstract NavigationBar createNavigationBar();

    /**
     * Sets up navigation actions for the UI.
     */
    protected abstract void setupNavigationActions();

    /**
     * Creates a welcome panel with a welcome message.
     *
     * @return the welcome panel
     */
    protected abstract JPanel createWelcomePanel();

    /**
     * Dynamically swap content panel contents.
     *
     * @param component the component to display in the content area
     */
    protected void showContent(JComponent component) {
        try {
            contentPanel.removeAll();

            JLabel titleLabel = new JLabel(getDashboardTitle());
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error displaying content: {0}", e.getMessage());
            StyleUtils.showStyledErrorDialog(this, "Failed to display content: " + e.getMessage());
        }
    }

    /**
     * Returns the title for the dashboard.
     *
     * @return the dashboard title
     */
    protected abstract String getDashboardTitle();
}