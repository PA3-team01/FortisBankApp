package com.fortisbank.business.services.notification;

 import com.fortisbank.business.services.customer.CustomerService;
 import com.fortisbank.business.services.manager.BankManagerService;
 import com.fortisbank.data.repositories.StorageMode;
 import com.fortisbank.models.accounts.Account;
 import com.fortisbank.models.others.Notification;
 import com.fortisbank.models.others.NotificationType;
 import com.fortisbank.models.transactions.Transaction;
 import com.fortisbank.models.users.BankManager;
 import com.fortisbank.models.users.Customer;
 import com.fortisbank.models.users.User;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.stream.Collectors;

 /**
  * Central service to manage and dispatch notifications to users.
  * Implemented as a singleton.
  */
 public class NotificationService {

     private StorageMode storageMode;

     private static NotificationService instance;

     /**
      * Private constructor to prevent instantiation.
      *
      * @param storageMode the storage mode
      */
     private NotificationService(StorageMode storageMode) {
         this.storageMode = storageMode;
     }

     /**
      * Returns the singleton instance of NotificationService for the given storage mode.
      *
      * @param storageMode the storage mode
      * @return the singleton instance of NotificationService
      */
     public static NotificationService getInstance(StorageMode storageMode) {
         if (instance == null) {
             instance = new NotificationService(storageMode);
         }
         return instance;
     }

     // === Notification Dispatching ===

     /**
      * Sends a notification to the user.
      *
      * @param recipient the recipient
      * @param type the type of notification
      * @param title the title of the notification
      * @param message the message of the notification
      */
     public void sendNotification(User recipient, NotificationType type, String title, String message) {
         if (recipient == null) return;
         Notification notification = new Notification(type, title, message);
         recipient.getInbox().add(notification);

         // update users in the database (Customer and BankManager)
         if (recipient instanceof Customer) {
             CustomerService.getInstance(storageMode).updateCustomer((Customer) recipient);
         }

         if (recipient instanceof BankManager) {
             BankManagerService.getInstance(storageMode).updateBankManager((BankManager) recipient);
         }
     }

     /**
      * Sends a notification to the user with additional customer and account information.
      *
      * @param recipient the recipient
      * @param type the type of notification
      * @param title the title of the notification
      * @param message the message of the notification
      * @param customer the customer related to the notification
      * @param account the account related to the notification
      */
     public void sendNotification(User recipient, NotificationType type, String title, String message, Customer customer, Account account) {
         if (recipient == null) return;

         Notification notification = new Notification(type, title, message, customer, account);
         recipient.getInbox().add(notification);

         // update users in the database (Customer and BankManager)
         if (recipient instanceof Customer) {
             CustomerService.getInstance(storageMode).updateCustomer((Customer) recipient);
         }

         if (recipient instanceof BankManager) {
             BankManagerService.getInstance(storageMode).updateBankManager((BankManager) recipient);
         }
     }

     // === Predefined Notification Helpers ===

     /**
      * Notifies the user of a completed transaction.
      *
      * @param user the user
      * @param tx the transaction
      */
     public void notifyTransactionReceipt(User user, Transaction tx) {
         String title = "Transaction Completed";
         String message = String.format("Your %s of $%.2f on %s was successful.",
                 tx.getTransactionType(), tx.getAmount(), tx.getTransactionDate());
         sendNotification(user, NotificationType.TRANSACTION_RECEIPT, title, message);
     }

     /**
      * Notifies the manager of a new account request from a customer.
      *
      * @param manager the manager
      * @param customer the customer
      * @param requestedAccount the requested account
      */
     public void notifyAccountRequest(User manager, Customer customer, Account requestedAccount) {
         if (manager == null || customer == null || requestedAccount == null) return;
         String title = "New Account Request";
         String message = String.format("Customer %s requested a new %s account.",
                 customer.getFullName(), requestedAccount.getAccountType());
         sendNotification(manager, NotificationType.ACCOUNT_OPENING_REQUEST, title, message, customer, requestedAccount);

         // Confirmation to customer
         sendNotification(customer, NotificationType.INFO, "Request Sent",
                 "Your account request was sent to the manager.", customer, requestedAccount);
     }

     /**
      * Notifies the customer of an approved account.
      *
      * @param customer the customer
      * @param approvedAccount the approved account
      */
     public void notifyApproval(Customer customer, Account approvedAccount) {
         String title = "Account Approved";
         String message = String.format("Your account (%s) has been approved.", approvedAccount.getAccountNumber());
         sendNotification(customer, NotificationType.ACCOUNT_APPROVAL, title, message, customer, approvedAccount);
     }

     /**
      * Notifies the customer of a rejected account request.
      *
      * @param customer the customer
      * @param reason the reason for rejection
      * @param rejectedAccount the rejected account
      */
     public void notifyRejection(Customer customer, String reason, Account rejectedAccount) {
         String title = "Account Rejected";
         String message = "Your account request was declined: " + reason;
         sendNotification(customer, NotificationType.ACCOUNT_REJECTION, title, message, customer, rejectedAccount);
     }

     /**
      * Notifies the user of a new message.
      *
      * @param user the user
      * @param fromName the name of the sender
      */
     public void notifyNewMessage(User user, String fromName) {
         String title = "New Message";
         String message = String.format("You received a new message from %s.", fromName);
         sendNotification(user, NotificationType.NEW_MESSAGE, title, message);
     }

     /**
      * Notifies the user of a security alert.
      *
      * @param user the user
      * @param details the details of the alert
      */
     public void notifySecurityAlert(User user, String details) {
         String title = "Security Alert";
         String message = "Important security notice: " + details;
         sendNotification(user, NotificationType.SECURITY_ALERT, title, message);
     }

     /**
      * Notifies the user of a system update.
      *
      * @param user the user
      * @param updateDetails the details of the update
      */
     public void notifySystemUpdate(User user, String updateDetails) {
         String title = "System Update";
         String message = "Recent changes: " + updateDetails;
         sendNotification(user, NotificationType.SYSTEM_UPDATE, title, message);
     }

     /**
      * Sends a custom notification to the user.
      *
      * @param user the user
      * @param title the title of the notification
      * @param message the message of the notification
      */
     public void notifyCustom(User user, String title, String message) {
         sendNotification(user, NotificationType.CUSTOM, title, message);
     }

     // === Inbox Helpers ===

     /**
      * Retrieves all notifications of the user, sorted from most recent to oldest.
      *
      * @param user the user
      * @return the list of notifications
      */
     public List<Notification> getAllNotifications(User user) {
         if (user == null || user.getInbox() == null) return new ArrayList<>();
         return reverseCopy(user.getInbox());
     }

     /**
      * Retrieves the unread notifications of the user.
      *
      * @param user the user
      * @return the list of unread notifications
      */
     public List<Notification> getUnreadNotifications(User user) {
         if (user == null || user.getInbox() == null) return new ArrayList<>();
         return user.getInbox().stream()
                 .filter(n -> !n.isRead())
                 .collect(Collectors.toList());
     }

     /**
      * Marks all notifications as read.
      *
      * @param user the user
      */
     public void markAllAsRead(User user) {
         if (user == null || user.getInbox() == null) return;
         user.getInbox().forEach(Notification::markAsRead);
     }

     /**
      * Clears the inbox of the user.
      *
      * @param user the user
      */
     public void clearInbox(User user) {
         if (user == null || user.getInbox() == null) return;
         user.getInbox().clear();
     }

     /**
      * Creates a reversed copy of the notification list, sorted by descending date.
      *
      * @param list the list of notifications
      * @return the reversed list of notifications
      */
     private List<Notification> reverseCopy(List<Notification> list) {
         List<Notification> reversed = new ArrayList<>(list);
         reversed.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
         return reversed;
     }
 }