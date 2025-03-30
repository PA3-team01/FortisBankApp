package com.fortisbank.utils;

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

    public static void styleButton(JButton button, boolean isPrimary) {
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBackground(isPrimary ? PRIMARY_COLOR : ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void applyGlobalFrameStyle(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
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

    public static void styleFrame(JFrame frame) {
        applyGlobalFrameStyle(frame);
        frame.setResizable(false);
    }

    public static JPanel createCustomTitleBar(JFrame frame, String titleText) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(frame.getWidth(), 30));

        JLabel title = new JLabel("  " + titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> frame.dispose());

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);

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
}
