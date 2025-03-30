package com.fortisbank.ui.panels;

import com.fortisbank.utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class ManagerUi extends JPanel {

    public ManagerUi() {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        // Title
        JLabel titleLabel = new JLabel("Manager Dashboard");
        StyleUtils.styleFormTitle(titleLabel);

        // Content placeholder
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel info = new JLabel("Welcome to the Manager Panel.");
        StyleUtils.styleLabel(info);


        content.add(Box.createVerticalStrut(15));
        content.add(info);

        add(titleLabel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}
