package com.fortisbank.ui.uiUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StyleUtils {

    // ========== COLOR PALETTE (Dark Theme) ==========
    public static final Color PRIMARY_COLOR = new Color(103, 58, 183); // Deep Purple
    public static final Color ACCENT_COLOR = new Color(255, 64, 129);  // Pink Accent
    public static final Color BACKGROUND_COLOR = new Color(48, 48, 48); // Dark background
    public static final Color TEXT_COLOR = new Color(240, 240, 240);    // Light text
    public static final Color ERROR_COLOR = new Color(244, 67, 54);     // Red
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);   // Green
    public static final Color NAVBAR_BG = new Color(33, 33, 33);        // Darker nav bar
    public static final Color NAVBAR_BUTTON_COLOR = new Color(66, 66, 66);

    // ========== FONTS ==========
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font STATUS_FONT = new Font("Segoe UI", Font.ITALIC, 12);

    // ========== COMPONENT STYLING METHODS ==========

    public static void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(new Color(66, 66, 66));
        field.setCaretColor(TEXT_COLOR);
        field.setPreferredSize(new Dimension(250, 28));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(97, 97, 97)),
                new EmptyBorder(5, 8, 5, 8)));
    }

    public static void stylePasswordField(JPasswordField field) {
        styleTextField(field);
    }

    public static void styleStatusLabel(JLabel label, boolean isValid) {
        label.setFont(STATUS_FONT);
        label.setForeground(isValid ? SUCCESS_COLOR : ERROR_COLOR);
    }
    public static void styleRadioButton(JRadioButton radioButton) {
        radioButton.setFont(FIELD_FONT);
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setBackground(BACKGROUND_COLOR);
        radioButton.setFocusPainted(false);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    public static void styleDropdown(JComboBox<?> comboBox) {
        comboBox.setFont(FIELD_FONT);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(new Color(66, 66, 66));
        comboBox.setFocusable(false);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(FIELD_FONT);
                label.setForeground(TEXT_COLOR);
                label.setBackground(isSelected ? new Color(97, 97, 97) : new Color(66, 66, 66));
                return label;
            }
        });
    }


    public static void styleButton(JButton button, boolean isPrimary) {
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBackground(isPrimary ? PRIMARY_COLOR : ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleNavButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBackground(NAVBAR_BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    public static void styleNavbar(JPanel navbar) {
        navbar.setBackground(NAVBAR_BG);
        navbar.setLayout(new BoxLayout(navbar, BoxLayout.Y_AXIS));
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static void applyGlobalFrameStyle(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    public static void applyGlobalDialogStyle(JDialog dialog) {
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        dialog.setUndecorated(true);
        dialog.setModal(true);
    }

    public static void styleFormTitle(JLabel titleLabel) {
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static void styleFormPanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }



    // TODO: Migrate this to a more reusable component
    public static JPanel createCustomTitleBar(JFrame frame, String titleText, JComponent rightControls) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(frame.getWidth(), 30));

        JLabel title = new JLabel("  " + titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Right-side button group
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonGroup.setOpaque(false);

        // Optional right controls (like Logout)
        if (rightControls != null) {
            buttonGroup.add(rightControls);
        }

        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> frame.dispose());

        buttonGroup.add(closeButton);

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(buttonGroup, BorderLayout.EAST);

        // Drag logic
        final Point[] initialClick = {null};
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;
                int xMoved = e.getX() - initialClick[0].x;
                int yMoved = e.getY() - initialClick[0].y;
                frame.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        return titleBar;
    }
    // TODO: Migrate this to a more reusable component
    public static void showStyledDialog(Component parent, String title, String message, boolean success) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(success ? SUCCESS_COLOR : ERROR_COLOR, 2));

        JLabel icon = new JLabel(UIManager.getIcon(success ? "OptionPane.informationIcon" : "OptionPane.errorIcon"));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(FIELD_FONT);
        messageLabel.setForeground(success ? SUCCESS_COLOR : ERROR_COLOR);

        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBackground(BACKGROUND_COLOR);
        messagePanel.add(icon, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        styleButton(okButton, success);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showStyledSuccessDialog(Component parent, String message) {
        showStyledDialog(parent, "Success", message, true);
    }

    public static void showStyledErrorDialog(Component parent, String message) {
        showStyledDialog(parent, "Error", message, false);
    }
}