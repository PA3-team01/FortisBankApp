package com.fortisbank.ui.components;

     import com.fortisbank.business.services.account.AccountLoanRequestService;
     import com.fortisbank.business.services.notification.NotificationService;
     import com.fortisbank.data.dal_utils.StorageMode;
     import com.fortisbank.contracts.models.accounts.Account;
     import com.fortisbank.contracts.models.others.Notification;
     import com.fortisbank.contracts.models.others.NotificationType;
     import com.fortisbank.contracts.models.users.Customer;
     import com.fortisbank.contracts.models.users.User;
     import com.fortisbank.business.services.session.SessionManager;
     import com.fortisbank.ui.ui_utils.StyleUtils;

     import javax.swing.*;
     import java.awt.*;
     import java.util.logging.Level;
     import java.util.logging.Logger;

     /**
      * The NotificationCard class is a JPanel component that displays a notification
      * with its details and provides actions based on the notification type.
      */
     public class NotificationCard extends JPanel {

         private static final Logger LOGGER = Logger.getLogger(NotificationCard.class.getName());
         private final StorageMode storageMode;

         /**
          * Constructs a NotificationCard for the given notification and storage mode.
          *
          * @param notification the notification to display
          * @param storageMode the storage mode to use for services
          */
         public NotificationCard(Notification notification, StorageMode storageMode) {
                this.storageMode = storageMode;
             try {
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
                     handleAccountOpeningRequest(notification, storageMode, footer);
                 }

                 JButton markReadBtn = new JButton(notification.isRead() ? "✔ Read" : "Mark as Read");
                 StyleUtils.styleButton(markReadBtn, !notification.isRead());
                 markReadBtn.addActionListener(e -> markNotificationAsRead(notification));

                 JButton deleteBtn = new JButton("Delete");
                 StyleUtils.styleButton(deleteBtn, false);
                 deleteBtn.addActionListener(e -> deleteNotification(notification));

                 footer.add(markReadBtn);
                 footer.add(deleteBtn);

                 add(title, BorderLayout.NORTH);
                 add(body, BorderLayout.CENTER);
                 add(footer, BorderLayout.SOUTH);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error initializing NotificationCard: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to initialize notification card: " + e.getMessage());
             }
         }

         private void handleAccountOpeningRequest(Notification notification, StorageMode storageMode, JPanel footer) {
             try {
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
                         addActionButtons(notification, customer, account, service, footer);
                     }
                 }
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error handling account opening request: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to handle account opening request: " + e.getMessage());
             }
         }

         private void addActionButtons(Notification notification, Customer customer, Account account, AccountLoanRequestService service, JPanel footer) {
             try {
                 JButton acceptBtn = new JButton("Accept");
                 JButton rejectBtn = new JButton("Reject");
                 StyleUtils.styleButton(acceptBtn, true);
                 StyleUtils.styleButton(rejectBtn, false);

                 acceptBtn.addActionListener(e -> {
                     try {
                         service.acceptAccountRequest(customer, account);
                         var serviceNotif = NotificationService.getInstance(storageMode);
                         serviceNotif.markAsRead(customer, notification);
                         StyleUtils.showStyledSuccessDialog(this, "Account opened successfully.");
                         refreshCardStatus(account);
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "Error accepting account request: {0}", ex.getMessage());
                         StyleUtils.showStyledErrorDialog(this, "Failed to accept account request: " + ex.getMessage());
                     }
                 });


                 rejectBtn.addActionListener(e -> {
                     try {
                         String reason = JOptionPane.showInputDialog(this, "Reason for rejection:");
                         if (reason != null && !reason.isBlank()) {
                             service.rejectAccountRequest(customer, reason, account);
                             var serviceNotif = NotificationService.getInstance(storageMode);
                             serviceNotif.markAsRead(customer, notification);
                             StyleUtils.showStyledSuccessDialog(this, "Account request rejected.");
                             refreshCardStatus(account);
                         }
                     } catch (Exception ex) {
                         LOGGER.log(Level.SEVERE, "Error rejecting account request: {0}", ex.getMessage());
                         StyleUtils.showStyledErrorDialog(this, "Failed to reject account request: " + ex.getMessage());
                     }
                 });


                 footer.add(acceptBtn);
                 footer.add(rejectBtn);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error adding action buttons: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to add action buttons: " + e.getMessage());
             }
         }

         private void markNotificationAsRead(Notification notification) {
             try {
                 var service = NotificationService.getInstance(storageMode);
                 var currentUser = SessionManager.getCurrentUser();
                 service.markAsRead(currentUser, notification);

                 StyleUtils.showStyledSuccessDialog(this, "Marked as read.");

                 SwingUtilities.invokeLater(() -> {
                     Container parent = this.getParent();
                     if (parent != null) {
                         parent.remove(this);
                         parent.revalidate();
                         parent.repaint();
                     }
                 });
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error marking notification as read: {0}", e.getMessage());
                 StyleUtils.showStyledErrorDialog(this, "Failed to mark notification as read: " + e.getMessage());
             }
         }



         public void deleteNotification(Notification notification) {
             try {
                 NotificationService.getInstance(storageMode).deleteNotification(notification);
                    SwingUtilities.invokeLater(() -> { // Remove the card from the parent container
                        Container parent = this.getParent();
                        if (parent != null) {
                            parent.remove(this);
                            parent.revalidate();
                            parent.repaint();
                        }
                    });
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error deleting notification: {0}", e.getMessage());
                 throw new RuntimeException("Failed to delete notification", e);
             }
         }


         private void refreshCardStatus(Account account) {
             try {
                 JPanel footer = (JPanel) getComponent(2); // Assumes footer is at SOUTH
                 footer.removeAll();

                 JLabel timestamp = new JLabel("Updated: " + new java.util.Date());
                 StyleUtils.styleLabel(timestamp);
                 timestamp.setFont(timestamp.getFont().deriveFont(Font.ITALIC, 10));
                 footer.add(timestamp);

                 JLabel statusLabel = new JLabel();
                 statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                 if (account.isActive()) {
                     statusLabel.setText("✔ Accepted");
                     statusLabel.setForeground(new Color(0, 128, 0));
                 } else {
                     statusLabel.setText("✖ Rejected");
                     statusLabel.setForeground(new Color(200, 0, 0));
                 }
                 footer.add(statusLabel);

                 footer.revalidate();
                 footer.repaint();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Failed to update notification footer status", e);
             }
         }

     }