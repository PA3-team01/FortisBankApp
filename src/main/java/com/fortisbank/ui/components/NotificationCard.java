package com.fortisbank.ui.components;

import com.fortisbank.business.services.account.AccountLoanRequestService;
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

public class NotificationCard extends JPanel {

    public NotificationCard(Notification notification, StorageMode storageMode) {
        setLayout(new BorderLayout());
        StyleUtils.styleFormPanel(this);

        JLabel title = new JLabel("[" + notification.getType().name() + "] " + notification.getTitle());
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
            User recipient = notification.getRelatedCustomer();
            Account account = notification.getRelatedAccount();

            if (recipient instanceof Customer customer) {
                AccountLoanRequestService service = AccountLoanRequestService.getInstance(storageMode);

                if (notification.isRead()) {
                    JLabel statusLabel = new JLabel();
                    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (account.isActive()) {
                        statusLabel.setText("✔ Accepted on: " + notification.getTimestamp());
                        statusLabel.setForeground(new Color(0, 128, 0)); // Green
                    } else {
                        statusLabel.setText("✖ Rejected on: " + notification.getTimestamp());
                        statusLabel.setForeground(new Color(200, 0, 0)); // Red
                    }
                    footer.add(statusLabel);
                } else {
                    JButton acceptBtn = new JButton("Accept");
                    JButton rejectBtn = new JButton("Reject");
                    StyleUtils.styleButton(acceptBtn, true);
                    StyleUtils.styleButton(rejectBtn, false);

                    acceptBtn.addActionListener(e -> {
                        service.acceptAccountRequest(customer, account);
                        notification.markAsRead();
                        StyleUtils.showStyledSuccessDialog(this, "Account opened successfully.");
                        SwingUtilities.invokeLater(() ->
                                ((JComponent) this.getParent()).revalidate()
                        );
                    });

                    rejectBtn.addActionListener(e -> {
                        String reason = JOptionPane.showInputDialog(this, "Reason for rejection:");
                        if (reason != null && !reason.isBlank()) {
                            service.rejectAccountRequest(customer, reason, account);
                            notification.markAsRead();
                            StyleUtils.showStyledSuccessDialog(this, "Account request rejected.");
                            SwingUtilities.invokeLater(() ->
                                    ((JComponent) this.getParent()).revalidate()
                            );
                        }
                    });

                    footer.add(acceptBtn);
                    footer.add(rejectBtn);
                }
            }
        }

        JButton markReadBtn = new JButton(notification.isRead() ? "✔ Read" : "Mark as Read");
        StyleUtils.styleButton(markReadBtn, !notification.isRead());
        markReadBtn.addActionListener(e -> {
            notification.markAsRead();
            SwingUtilities.invokeLater(() ->
                    ((JComponent) this.getParent()).revalidate()
            );
        });

        JButton deleteBtn = new JButton("Delete");
        StyleUtils.styleButton(deleteBtn, false);
        deleteBtn.addActionListener(e -> {
            SessionManager.getCurrentUser().getInbox().remove(notification);
            SwingUtilities.invokeLater(() ->
                    ((JComponent) this.getParent()).revalidate()
            );
        });

        footer.add(markReadBtn);
        footer.add(deleteBtn);

        add(title, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}
