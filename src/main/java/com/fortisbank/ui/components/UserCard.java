package com.fortisbank.ui.components;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.users.BankManager;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.models.users.User;
import com.fortisbank.ui.frames.subFrames.UserUpdateForm;
import com.fortisbank.ui.ui_utils.StyleUtils;

import javax.swing.*;
import java.awt.*;

/**
 * The UserCard class is a JPanel component that displays user information
 * and provides an edit button to update user details.
 */
public class UserCard extends JPanel {

    /**
     * Constructs a UserCard for the given user and storage mode.
     *
     * @param user the user to display information for
     * @param storageMode the storage mode to use for services
     */
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