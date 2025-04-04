package com.fortisbank.ui.panels.commons;

import com.fortisbank.business.services.AccountLoanRequestService;
import com.fortisbank.business.services.NotificationService;
import com.fortisbank.data.repositories.StorageMode;
import com.fortisbank.models.others.Notification;
import com.fortisbank.session.SessionManager;
import com.fortisbank.ui.components.NotificationCard;
import com.fortisbank.ui.uiUtils.StyleUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InboxPanel extends JPanel {

    private final NotificationService notificationService;
    private final AccountLoanRequestService accountLoanService;
    private final JPanel messageListPanel = new JPanel();
    private final JComboBox<String> filterSelector = new JComboBox<>(new String[]{"All", "Unread", "Custom", "System", "Security"});
    private final StorageMode storageMode;

    public InboxPanel(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.notificationService = NotificationService.getInstance(storageMode);
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
                NotificationCard card = new NotificationCard(notification, storageMode);
                messageListPanel.add(card);
                messageListPanel.add(Box.createVerticalStrut(10));
            }
        }

        messageListPanel.revalidate();
        messageListPanel.repaint();
    }
}
