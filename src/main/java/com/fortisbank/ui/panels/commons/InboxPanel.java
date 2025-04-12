package com.fortisbank.ui.panels.commons;

     import com.fortisbank.business.services.account.AccountLoanRequestService;
     import com.fortisbank.business.services.notification.NotificationService;
     import com.fortisbank.data.dal_utils.StorageMode;
     import com.fortisbank.contracts.models.others.Notification;
     import com.fortisbank.business.services.session.SessionManager;
     import com.fortisbank.ui.components.NotificationCard;
     import com.fortisbank.ui.ui_utils.StyleUtils;

     import javax.swing.*;
     import java.awt.*;
     import java.util.List;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The InboxPanel class represents the inbox panel of the Fortis Bank application.
      * It extends JPanel and provides a user interface to display and manage notifications.
      */
     public class InboxPanel extends JPanel {

         private static final Logger LOGGER = Logger.getLogger(InboxPanel.class.getName());

         private final NotificationService notificationService;
         private final AccountLoanRequestService accountLoanService;
         private final JPanel messageListPanel = new JPanel();
         private final JComboBox<String> filterSelector = new JComboBox<>(new String[]{"All", "Unread", "Custom", "System", "Security"});
         private final StorageMode storageMode;

         /**
          * Constructs an InboxPanel with the specified storage mode.
          *
          * @param storageMode the storage mode to use for services
          */
         public InboxPanel(StorageMode storageMode) {
             this.storageMode = storageMode;
             this.notificationService = NotificationService.getInstance(storageMode);
             this.accountLoanService = AccountLoanRequestService.getInstance(storageMode);

             try {
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
                     try {
                         notificationService.markAllAsRead(SessionManager.getCurrentUser());
                         refreshMessages();
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "Error marking all notifications as read: {0}", ex.getMessage());
                         StyleUtils.showStyledErrorDialog(this, "Failed to mark all notifications as read: " + ex.getMessage());
                     }
                 });

                 clearBtn.addActionListener(e -> {
                     try {
                         notificationService.clearInbox(SessionManager.getCurrentUser());
                         refreshMessages();
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "Error clearing inbox: {0}", ex.getMessage());
                         StyleUtils.showStyledErrorDialog(this, "Failed to clear inbox: " + ex.getMessage());
                     }
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
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error initializing InboxPanel: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to initialize the inbox panel: " + e.getMessage());
             }
         }

         /**
          * Refreshes the messages displayed in the inbox panel based on the selected filter.
          */
         private void refreshMessages() {
             try {
                 messageListPanel.removeAll();

                 List<Notification> notifications = switch (filterSelector.getSelectedItem().toString()) {
                     case "Unread" -> notificationService.getUnreadNotifications(SessionManager.getCurrentUser().getUserId());
                     default -> notificationService.getAllNotifications(SessionManager.getCurrentUser().getUserId());
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
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error refreshing messages: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to refresh messages: " + e.getMessage());
             }
         }
     }