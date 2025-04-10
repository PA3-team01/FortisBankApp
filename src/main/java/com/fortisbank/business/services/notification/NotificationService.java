package com.fortisbank.business.services.notification;

     import com.fortisbank.business.services.users.customer.CustomerService;
     import com.fortisbank.business.services.users.manager.BankManagerService;
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

     /**
      * Central service to manage and dispatch notifications to users.
      * Implemented as a singleton.
      */
     public class NotificationService {

         private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
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

         public void sendNotification(User recipient, NotificationType type, String title, String message) {
             try {
                 if (recipient == null) {
                     throw new IllegalArgumentException("Recipient cannot be null.");
                 }
                 Notification notification = new Notification(type, title, message);
                 recipient.getInbox().add(notification);

                 if (recipient instanceof Customer) {
                     CustomerService.getInstance(storageMode).updateCustomer((Customer) recipient);
                 } else if (recipient instanceof BankManager) {
                     BankManagerService.getInstance(storageMode).updateBankManager((BankManager) recipient);
                 }
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error sending notification: {0}", e.getMessage());
                 throw new RuntimeException("Failed to send notification", e);
             }
         }

         public void sendNotification(User recipient, NotificationType type, String title, String message, Customer customer, Account account) {
             try {
                 if (recipient == null) {
                     throw new IllegalArgumentException("Recipient cannot be null.");
                 }
                 Notification notification = new Notification(type, title, message, customer, account);
                 recipient.getInbox().add(notification);

                 if (recipient instanceof Customer) {
                     CustomerService.getInstance(storageMode).updateCustomer((Customer) recipient);
                 } else if (recipient instanceof BankManager) {
                     BankManagerService.getInstance(storageMode).updateBankManager((BankManager) recipient);
                 }
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error sending notification with additional details: {0}", e.getMessage());
                 throw new RuntimeException("Failed to send notification with additional details", e);
             }
         }

         // === Predefined Notification Helpers ===

         public void notifyTransactionReceipt(User user, Transaction tx) {
             try {
                 String title = "Transaction Completed";
                 String message = String.format("Your %s of $%.2f on %s was successful.",
                         tx.getTransactionType(), tx.getAmount(), tx.getTransactionDate());
                 sendNotification(user, NotificationType.TRANSACTION_RECEIPT, title, message);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error notifying transaction receipt: {0}", e.getMessage());
                 throw new RuntimeException("Failed to notify transaction receipt", e);
             }
         }

         public void notifyAccountRequest(User manager, Customer customer, Account requestedAccount) {
             try {
                 if (manager == null || customer == null || requestedAccount == null) {
                     throw new IllegalArgumentException("Manager, customer, and requested account cannot be null.");
                 }
                 String title = "New Account Request";
                 String message = String.format("Customer %s requested a new %s account.",
                         customer.getFullName(), requestedAccount.getAccountType());
                 sendNotification(manager, NotificationType.ACCOUNT_OPENING_REQUEST, title, message, customer, requestedAccount);

                 sendNotification(customer, NotificationType.INFO, "Request Sent",
                         "Your account request was sent to the manager.", customer, requestedAccount);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error notifying account request: {0}", e.getMessage());
                 throw new RuntimeException("Failed to notify account request", e);
             }
         }

         public void notifyApproval(Customer customer, Account approvedAccount) {
             try {
                 String title = "Account Approved";
                 String message = String.format("Your account (%s) has been approved.", approvedAccount.getAccountNumber());
                 sendNotification(customer, NotificationType.ACCOUNT_APPROVAL, title, message, customer, approvedAccount);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error notifying account approval: {0}", e.getMessage());
                 throw new RuntimeException("Failed to notify account approval", e);
             }
         }

         public void notifyRejection(Customer customer, String reason, Account rejectedAccount) {
             try {
                 String title = "Account Rejected";
                 String message = "Your account request was declined: " + reason;
                 sendNotification(customer, NotificationType.ACCOUNT_REJECTION, title, message, customer, rejectedAccount);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error notifying account rejection: {0}", e.getMessage());
                 throw new RuntimeException("Failed to notify account rejection", e);
             }
         }

         // === Inbox Helpers ===

         public List<Notification> getAllNotifications(User user) {
             try {
                 if (user == null || user.getInbox() == null) {
                     return new ArrayList<>();
                 }
                 return reverseCopy(user.getInbox());
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error retrieving all notifications: {0}", e.getMessage());
                 throw new RuntimeException("Failed to retrieve all notifications", e);
             }
         }

         public List<Notification> getUnreadNotifications(User user) {
             try {
                 if (user == null || user.getInbox() == null) {
                     return new ArrayList<>();
                 }
                 return user.getInbox().stream()
                         .filter(n -> !n.isRead())
                         .collect(Collectors.toList());
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error retrieving unread notifications: {0}", e.getMessage());
                 throw new RuntimeException("Failed to retrieve unread notifications", e);
             }
         }

         public void markAllAsRead(User user) {
             try {
                 if (user == null || user.getInbox() == null) {
                     return;
                 }
                 user.getInbox().forEach(Notification::markAsRead);
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error marking all notifications as read: {0}", e.getMessage());
                 throw new RuntimeException("Failed to mark all notifications as read", e);
             }
         }

         public void clearInbox(User user) {
             try {
                 if (user == null || user.getInbox() == null) {
                     return;
                 }
                 user.getInbox().clear();
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error clearing inbox: {0}", e.getMessage());
                 throw new RuntimeException("Failed to clear inbox", e);
             }
         }

         private List<Notification> reverseCopy(List<Notification> list) {
             try {
                 List<Notification> reversed = new ArrayList<>(list);
                 reversed.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                 return reversed;
             } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error reversing notification list: {0}", e.getMessage());
                 throw new RuntimeException("Failed to reverse notification list", e);
             }
         }

         public void notifyNewMessage(BankManager manager, String fullName) {
                try {
                    String title = "New Message";
                    String message = String.format("You have a new message from %s.", fullName);
                    sendNotification(manager, NotificationType.NEW_MESSAGE, title, message);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error notifying new message: {0}", e.getMessage());
                    throw new RuntimeException("Failed to notify new message", e);
                }
         }
     }