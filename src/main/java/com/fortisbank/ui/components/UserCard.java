package com.fortisbank.ui.components;

import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.users.BankManager;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;
import com.fortisbank.ui.frames.subFrames.UserUpdateForm;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;

public class UserCard extends JPanel {

    public UserCard(User user, StorageMode storageMode) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(StyleUtils.NAVBAR_BG));
        setBackground(StyleUtils.NAVBAR_BUTTON_COLOR);
        setOpaque(true);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        String fullName = "Unknown User";
        if (user instanceof Customer c) {
            fullName = c.getFullName();
        } else if (user instanceof BankManager m) {
            fullName = m.getFullName();
        }

        JLabel nameLabel = new JLabel(fullName);
        JLabel emailLabel = new JLabel("Email: " + user.getEmail());
        JLabel roleLabel = new JLabel("Role: " + user.getRole().name());

        StyleUtils.styleLabel(nameLabel);
        StyleUtils.styleLabel(emailLabel);
        StyleUtils.styleLabel(roleLabel);

        leftPanel.add(nameLabel);
        leftPanel.add(emailLabel);
        leftPanel.add(roleLabel);

        // Edit button
        JButton editButton = new JButton("Edit");
        StyleUtils.styleButton(editButton, true);
        editButton.addActionListener(e -> {
            new UserUpdateForm(user, storageMode).setVisible(true);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(editButton);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
}
