package com.fortisbank.ui.panels.commons;

import com.fortisbank.business.services.AccountLoanRequestService;
import com.fortisbank.business.services.NotificationService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.accounts.Account;
import com.fortisbank.models.others.Notification;
import com.fortisbank.models.others.NotificationType;
import com.fortisbank.models.users.Customer;
import com.fortisbank.models.users.User;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InboxPanel extends JPanel {

    private final NotificationService notificationService = NotificationService.getInstance();
    private  AccountLoanRequestService accountLoanService;
    private final JPanel messageListPanel = new JPanel();
    private final JComboBox<String> filterSelector = new JComboBox<>(new String[] {"All", "Unread", "Custom", "System", "Security"});
    private StorageMode storageMode;

    public InboxPanel(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.accountLoanService = AccountLoanRequestService.getInstance(storageMode);
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        JLabel title = new JLabel("Inbox");
        StyleUtils.styleFormTitle(title);
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter:");
        StyleUtils.styleLabel(filterLabel);
        StyleUtils.styleDropdown(filterSelector);
        filterSelector.addActionListener(e -> refreshMessages());

        JButton markAllReadBtn = new JButton("Mark All as Read");
        JButton clearBtn = new JButton("Clear Inbox");

        StyleUtils.styleButton(markAllReadBtn, false);
        StyleUtils.styleButton(clearBtn, false);

        markAllReadBtn.addActionListener(e -> {
            notificationService.markAllAsRead(SessionManager.getCurrentUser());
            refreshMessages();
        });

        clearBtn.addActionListener(e -> {
            notificationService.clearInbox(SessionManager.getCurrentUser());
            refreshMessages();
        });

        topPanel.add(filterLabel);
        topPanel.add(filterSelector);
        topPanel.add(markAllReadBtn);
        topPanel.add(clearBtn);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        messageListPanel.setLayout(new BoxLayout(messageListPanel, BoxLayout.Y_AXIS));
        messageListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(messageListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshMessages();
    }

    private void refreshMessages() {
        messageListPanel.removeAll();

        List<Notification> notifications = switch (filterSelector.getSelectedItem().toString()) {
            case "Unread" -> notificationService.getUnreadNotifications(SessionManager.getCurrentUser());
            default -> notificationService.getAllNotifications(SessionManager.getCurrentUser());
        };

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications.");
            StyleUtils.styleLabel(emptyLabel);
            messageListPanel.add(emptyLabel);
        } else {
            for (Notification notification : notifications) {
                JPanel card = buildNotificationCard(notification);
                messageListPanel.add(card);
                messageListPanel.add(Box.createVerticalStrut(10));
            }
        }

        messageListPanel.revalidate();
        messageListPanel.repaint();
    }

    private JPanel buildNotificationCard(Notification notification) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(card);

        String typeText = notification.getType().name();
        JLabel title = new JLabel("[" + typeText + "] " + notification.getTitle());
        StyleUtils.styleLabel(title);

        JLabel body = new JLabel("<html><p style='width: 300px;'>" + notification.getMessage() + "</p></html>");
        StyleUtils.styleLabel(body);

        JLabel timestamp = new JLabel(notification.getTimestamp().toString());
        StyleUtils.styleLabel(timestamp);
        timestamp.setFont(timestamp.getFont().deriveFont(Font.ITALIC, 10));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(timestamp);

        if (notification.getType() == NotificationType.ACCOUNT_OPENING_REQUEST) {
            JButton acceptBtn = new JButton("Accept");
            JButton rejectBtn = new JButton("Reject");
            StyleUtils.styleButton(acceptBtn, true);
            StyleUtils.styleButton(rejectBtn, false);

            acceptBtn.addActionListener(e -> {
                //TODO: Implement the logic to accept the request
                // Ideally would have a mapping from notification to the Account and Customer
                JOptionPane.showMessageDialog(this, "Accepted request. Integration logic needed.");
                notification.markAsRead();
                refreshMessages();
            });

            rejectBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(this, "Reason for rejection:");
                if (reason != null && !reason.isBlank()) {
                    //TODO: Implement the logic to reject the request
                    JOptionPane.showMessageDialog(this, "Rejected with reason: " + reason + ". Integration logic needed.");
                    notification.markAsRead();
                    refreshMessages();
                }
            });

            footer.add(acceptBtn);
            footer.add(rejectBtn);
        }

        JButton markReadBtn = new JButton(notification.isRead() ? "✔ Read" : "Mark as Read");
        StyleUtils.styleButton(markReadBtn, !notification.isRead());
        markReadBtn.addActionListener(e -> {
            notification.markAsRead();
            refreshMessages();
        });

        JButton deleteBtn = new JButton("Delete");
        StyleUtils.styleButton(deleteBtn, false);
        deleteBtn.addActionListener(e -> {
            SessionManager.getCurrentUser().getInbox().remove(notification);
            refreshMessages();
        });

        footer.add(markReadBtn);
        footer.add(deleteBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }
}
