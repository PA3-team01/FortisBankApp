package com.fortisbank.ui.panels;

import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class CustomerUi extends JPanel {

    public CustomerUi() {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // Title
        JLabel titleLabel = new JLabel("Customer Dashboard");
        StyleUtils.styleFormTitle(titleLabel);

        // Content placeholder
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false); // Inherit background from parent

        JLabel info = new JLabel("This is a customer panel.");
        StyleUtils.styleLabel(info);



        content.add(Box.createVerticalStrut(15));
        content.add(info);

        // Assemble layout
        add(titleLabel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
