package com.fortisbank.business.services.notification;

import com.fortisbank.data.dal_utils.RepositoryFactory;
import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.models.others.NotificationType;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.contracts.models.users.BankManager;
import com.fortisbank.contracts.models.users.Customer;
import com.fortisbank.contracts.models.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final StorageMode storageMode;
    private final RepositoryFactory repositoryFactory;

    private static NotificationService instance;

    private NotificationService(StorageMode storageMode) {
        this.storageMode = storageMode;
        this.repositoryFactory = RepositoryFactory.getInstance(storageMode);
    }

    public static synchronized NotificationService getInstance(StorageMode storageMode) {
        if (instance == null) {
            instance = new NotificationService(storageMode);
        }
        return instance;
    }

    public void sendNotification(User recipient, NotificationType type, String title, String message) {
        sendNotification(recipient, type, title, message, null, null);
    }

    public void sendNotification(User recipient, NotificationType type, String title, String message, Customer relatedCustomer, Account relatedAccount) {
        try {
            if (recipient == null) {
                throw new IllegalArgumentException("Recipient cannot be null.");
            }

            Notification notification = new Notification(type, title, message, relatedCustomer, relatedAccount);
            notification.setRecipientUserId(recipient.getUserId()); // Set recipient explicitly
            repositoryFactory.getNotificationRepository().insertNotification(notification);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending notification: {0}", e.getMessage());
            throw new RuntimeException("Failed to send notification", e);
        }
    }


    public void notifyTransactionReceipt(Customer customer, Transaction tx) {
        String title = "Transaction Completed";
        String message = String.format("Your %s of $%.2f on %s was successful.",
                tx.getTransactionType(), tx.getAmount(), tx.getTransactionDate());
        sendNotification(customer, NotificationType.TRANSACTION_RECEIPT, title, message, customer, tx.getSourceAccount());
    }

    public void notifyAccountRequest(BankManager manager, Customer customer, Account requestedAccount) {
        String title = "New Account Request";
        String message = String.format("Customer %s requested a new %s account.",
                customer.getFullName(), requestedAccount.getAccountType());
        sendNotification(manager, NotificationType.ACCOUNT_OPENING_REQUEST, title, message, customer, requestedAccount);

        sendNotification(customer, NotificationType.INFO, "Request Sent",
                "Your account request was sent to the manager.", customer, requestedAccount);
    }

    public void notifyApproval(Customer customer, Account approvedAccount) {
        String title = "Account Approved";
        String message = String.format("Your account (%s) has been approved.", approvedAccount.getAccountNumber());
        sendNotification(customer, NotificationType.ACCOUNT_APPROVAL, title, message, customer, approvedAccount);
    }

    public void notifyRejection(Customer customer, String reason, Account rejectedAccount) {
        String title = "Account Rejected";
        String message = "Your account request was declined: " + reason;
        sendNotification(customer, NotificationType.ACCOUNT_REJECTION, title, message, customer, rejectedAccount);
    }

    public void notifyNewMessage(BankManager manager, Customer sender) {
        String title = "New Message";
        String message = String.format("You have a new message from %s.", sender.getFullName());
        sendNotification(manager, NotificationType.NEW_MESSAGE, title, message, sender, null);
    }

    public List<Notification> getAllNotificationsForUser(User user) {
        try {
            return repositoryFactory.getNotificationRepository().getNotificationsByUserId(user.getUserId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications: {0}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Notification> getUnreadNotificationsForUser(User user) {
        return getAllNotificationsForUser(user).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public void markAllAsRead(User user) {
        try {
            var repo = repositoryFactory.getNotificationRepository();
            List<Notification> notifications = repo.getNotificationsByUserId(user.getUserId());
            for (Notification notification : notifications) {
                notification.markAsRead();
                repo.markAsSeen(notification.getNotificationId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error marking notifications as read: {0}", e.getMessage());
        }
    }

    public void markAsRead(User user, Notification notification) {
        try {
            var repo = repositoryFactory.getNotificationRepository();
            notification.markAsRead();
            repo.markAsSeen(notification.getNotificationId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read: {0}", e.getMessage());
        }
    }

    public void clearInbox(User user) {
        try {
            var repo = repositoryFactory.getNotificationRepository();
            List<Notification> notifications = repo.getNotificationsByUserId(user.getUserId());
            for (Notification notification : notifications) {
                repo.deleteNotification(notification.getNotificationId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error clearing inbox: {0}", e.getMessage());
        }
    }
    //delete one notification
    public void deleteNotification(Notification notification) {
        try {
            var repo = repositoryFactory.getNotificationRepository();
            repo.deleteNotification(notification.getNotificationId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting notification: {0}", e.getMessage());
        }
    }

    public List<Notification> getAllNotifications(String userId) {
        try {
            // get the repository
            return repositoryFactory.getNotificationRepository().getNotificationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications for user ID: " + userId, e);
            return new ArrayList<>();
        }
    }

    public List<Notification> getUnreadNotifications(String userId) {
        try {
            return repositoryFactory.getNotificationRepository().getNotificationsByUserId(userId).stream()
                    .filter(n -> !n.isRead())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving unread notifications for user ID: " + userId, e);
            return new ArrayList<>();
        }
    }



}
