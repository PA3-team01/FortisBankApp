package com.fortisbank.ui.frames.subFrames;

import com.fortisbank.business.services.customer.CustomerService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.ui.components.NavigationBar;
import com.fortisbank.ui.panels.commons.InboxPanel;
import com.fortisbank.ui.panels.commons.ProfilePanel;
import com.fortisbank.ui.panels.commons.SettingPanel;
import com.fortisbank.ui.panels.managerPanels.InterestRateManager;
import com.fortisbank.ui.panels.managerPanels.ReportsPanel;
import com.fortisbank.ui.panels.managerPanels.UserManagementPanel;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

/**
 * The ManagerUi class represents the user interface for managers.
 * It extends JPanel and provides navigation and content display functionality.
 */
public class ManagerUi extends JPanel {

    private final NavigationBar navPanel;
    private final JPanel contentPanel;
    private StorageMode storageMode;

    /**
     * Constructs a ManagerUi with the specified storage mode.
     *
     * @param storageMode the storage mode to use for services
     */
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
        navPanel.setButtonAction("Reports", () -> showContent(new ReportsPanel(storageMode,
                CustomerService.getInstance(storageMode).getAllCustomers())));
        navPanel.setButtonAction("Interest Rates", () -> showContent(new InterestRateManager()));
        navPanel.setButtonAction("Settings", () -> showContent(new SettingPanel()));

        navPanel.setButtonAction("Profile", () -> showContent(new ProfilePanel()));
    }

    /**
     * Dynamically swap content panel contents.
     *
     * @param component the component to display in the content area
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

    /**
     * Creates a welcome panel with a welcome message.
     *
     * @return the welcome panel
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // spacing

        // Title
        JLabel title = new JLabel("Welcome, Bank Manager!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(StyleUtils.PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subheading
        JLabel subtitle = new JLabel("FortisBank Manager Dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(StyleUtils.TEXT_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea desc = new JTextArea(
                """
                This panel gives you access to all essential managerial tools:
                
                - View and manage registered customers
                - Generate detailed financial reports
                - Review customer account activities
                - Monitor system-wide metrics and summaries
                - Configure interest rates and security settings
                
                Use the sidebar to navigate between functionalities.
                """);
        desc.setFont(StyleUtils.FIELD_FONT);
        desc.setForeground(StyleUtils.TEXT_COLOR);
        desc.setBackground(StyleUtils.BACKGROUND_COLOR);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setOpaque(false);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setMaximumSize(new Dimension(600, 250));

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(25));
        panel.add(desc);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

}