package com.fortisbank.business.services.notification;

import com.fortisbank.data.dal_utils.StorageMode;
import com.fortisbank.data.interfaces.INotificationRepository;
import com.fortisbank.contracts.models.accounts.Account;
import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.models.others.NotificationType;
import com.fortisbank.contracts.models.transactions.Transaction;
import com.fortisbank.contracts.models.users.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Central service to manage and dispatch notifications to users.
 */
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private final INotificationRepository notificationRepository;
    private final StorageMode storageMode;

    private static NotificationService instance;

    private NotificationService(StorageMode storageMode, INotificationRepository notificationRepository) {
        this.storageMode = storageMode;
        this.notificationRepository = notificationRepository;
    }

    public static synchronized NotificationService getInstance(StorageMode storageMode, INotificationRepository notificationRepository) {
        if (instance == null) {
            instance = new NotificationService(storageMode, notificationRepository);
        }
        return instance;
    }

    public void sendNotification(String userId, NotificationType type, String title, String message, Customer customer, Account account) {
        try {
            Notification notification = new Notification(type, title, message, customer, account);
            notification.setRelatedCustomer(customer);
            notification.setRelatedAccount(account);
            notificationRepository.insertNotification(notification);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending notification: {0}", e.getMessage());
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    public void notifyTransactionReceipt(String userId, Transaction tx, Customer customer) {
        String title = "Transaction Completed";
        String message = String.format("Your %s of $%.2f on %s was successful.",
                tx.getTransactionType(), tx.getAmount(), tx.getTransactionDate());
        sendNotification(userId, NotificationType.TRANSACTION_RECEIPT, title, message, customer, tx.getSourceAccount());
    }

    public void notifyAccountRequest(String managerId, Customer customer, Account requestedAccount) {
        String title = "New Account Request";
        String message = String.format("Customer %s requested a new %s account.",
                customer.getFullName(), requestedAccount.getAccountType());
        sendNotification(managerId, NotificationType.ACCOUNT_OPENING_REQUEST, title, message, customer, requestedAccount);

        sendNotification(customer.getUserId(), NotificationType.INFO, "Request Sent",
                "Your account request was sent to the manager.", customer, requestedAccount);
    }

    public void notifyApproval(String customerId, Customer customer, Account approvedAccount) {
        String title = "Account Approved";
        String message = String.format("Your account (%s) has been approved.", approvedAccount.getAccountNumber());
        sendNotification(customerId, NotificationType.ACCOUNT_APPROVAL, title, message, customer, approvedAccount);
    }

    public void notifyRejection(String customerId, Customer customer, String reason, Account rejectedAccount) {
        String title = "Account Rejected";
        String message = "Your account request was declined: " + reason;
        sendNotification(customerId, NotificationType.ACCOUNT_REJECTION, title, message, customer, rejectedAccount);
    }

    public void notifyNewMessage(String managerId, Customer sender) {
        String title = "New Message";
        String message = String.format("You have a new message from %s.", sender.getFullName());
        sendNotification(managerId, NotificationType.NEW_MESSAGE, title, message, sender, null);
    }

    public List<Notification> getAllNotificationsForUser(String userId) {
        try {
            return notificationRepository.getNotificationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications: {0}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Notification> getUnreadNotificationsForUser(String userId) {
        return getAllNotificationsForUser(userId).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public void markAllAsRead(String userId) {
        try {
            List<Notification> notifications = notificationRepository.getNotificationsByUserId(userId);
            for (Notification notification : notifications) {
                notification.markAsRead();
                notificationRepository.markAsSeen(notification.getNotificationId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error marking notifications as read: {0}", e.getMessage());
        }
    }

    public void clearInbox(String userId) {
        try {
            List<Notification> notifications = notificationRepository.getNotificationsByUserId(userId);
            for (Notification notification : notifications) {
                notificationRepository.deleteNotification(notification.getNotificationId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error clearing inbox: {0}", e.getMessage());
        }
    }
}
