package com.fortisbank.ui.components;

import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The NavigationBar class is a JPanel component that creates a vertical navigation bar
 * with buttons for each provided label. It allows external classes to attach actions
 * to these buttons.
 */
public class NavigationBar extends JPanel {

    private final Map<String, JButton> buttons = new LinkedHashMap<>();

    /**
     * Constructs a NavigationBar with buttons for each provided label.
     *
     * @param labels the labels for the navigation buttons
     */
    public NavigationBar(String... labels) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        StyleUtils.styleNavbar(this);

        for (String label : labels) {
            JButton button = new JButton(label);
            StyleUtils.styleNavButton(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons.put(label, button);
            add(Box.createVerticalStrut(10));
            add(button);
        }

        add(Box.createVerticalGlue());
    }

    /**
     * Allows external classes to attach actions to navigation buttons by label.
     *
     * @param label the label of the button
     * @param action the action to attach to the button
     */
    public void setButtonAction(String label, Runnable action) {
        JButton button = buttons.get(label);
        if (button != null) {
            button.addActionListener(e -> action.run());
        }
    }

    /**
     * Optional: Get direct access to a button.
     *
     * @param label the label of the button
     * @return the JButton associated with the label, or null if not found
     */
    public JButton getButton(String label) {
        return buttons.get(label);
    }
}