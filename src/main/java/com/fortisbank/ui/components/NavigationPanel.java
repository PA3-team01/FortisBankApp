package com.fortisbank.ui.components;

import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NavigationPanel extends JPanel {

    private final Map<String, JButton> buttons = new LinkedHashMap<>();

    public NavigationPanel(String... labels) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        StyleUtils.styleFormPanel(this);

        for (String label : labels) {
            JButton button = new JButton(label);
            StyleUtils.styleButton(button, false);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons.put(label, button);
            add(Box.createVerticalStrut(10));
            add(button);
        }

        add(Box.createVerticalGlue());
    }

    /**
     * Allows external classes to attach actions to navigation buttons by label.
     */
    public void setButtonAction(String label, Runnable action) {
        JButton button = buttons.get(label);
        if (button != null) {
            button.addActionListener(e -> action.run());
        }
    }

    /**
     * Optional: Get direct access to a button.
     */
    public JButton getButton(String label) {
        return buttons.get(label);
    }
}
