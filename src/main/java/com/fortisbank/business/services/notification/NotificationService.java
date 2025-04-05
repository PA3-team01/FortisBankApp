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

    private static  NotificationService instance ;

    private NotificationService(StorageMode storageMode) {
        this.storageMode = storageMode;
    }

    public static NotificationService getInstance(StorageMode storageMode) {
        if (instance == null) {
            instance = new NotificationService(storageMode);
        }
        return instance;
    }

    // === Notification Dispatching ===

    /**
     * Envoie une notification au user
     * @param recipient Destinataire
     * @param type Type
     * @param title Titre
     * @param message Message
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
     * Notification pour les transactions
     * @param user User
     * @param tx
     */
    public void notifyTransactionReceipt(User user, Transaction tx) {
        String title = "Transaction Completed";
        String message = String.format("Your %s of $%.2f on %s was successful.",
                tx.getTransactionType(), tx.getAmount(), tx.getTransactionDate());
        sendNotification(user, NotificationType.TRANSACTION_RECEIPT, title, message);
    }

    /**
     * Notification pour la demande d'ouverture de compte envoyer au manager
     * @param manager Manager
     * @param customer Customer
     * @param requestedAccount Compte demander
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
     * Notification envoyer au user apres l'approbation du compte
     * @param customer Customer
     * @param approvedAccount Compter Approuver
     */
    public void notifyApproval(Customer customer, Account approvedAccount) {
        String title = "Account Approved";
        String message = String.format("Your account (%s) has been approved.", approvedAccount.getAccountNumber());
        sendNotification(customer, NotificationType.ACCOUNT_APPROVAL, title, message, customer, approvedAccount);
    }

    /**
     * Notification de rejet de demande de compte envoyer au user
     * @param customer Customer
     * @param reason Raison
     * @param rejectedAccount Compte Rejeter
     */
    public void notifyRejection(Customer customer, String reason, Account rejectedAccount) {
        String title = "Account Rejected";
        String message = "Your account request was declined: " + reason;
        sendNotification(customer, NotificationType.ACCOUNT_REJECTION, title, message, customer, rejectedAccount);
    }

    /**
     * Notification d'un nouveau message
     * @param user User
     * @param fromName de > x
     */
    public void notifyNewMessage(User user, String fromName) {
        String title = "New Message";
        String message = String.format("You received a new message from %s.", fromName);
        sendNotification(user, NotificationType.NEW_MESSAGE, title, message);
    }

    /**
     * Notification d'alerte de securite
     * @param user User
     * @param details Details
     */
    public void notifySecurityAlert(User user, String details) {
        String title = "Security Alert";
        String message = "Important security notice: " + details;
        sendNotification(user, NotificationType.SECURITY_ALERT, title, message);
    }

    /**
     * Notification de mise a jour du systeme
     * @param user User
     * @param updateDetails Details mise a jour
     */
    public void notifySystemUpdate(User user, String updateDetails) {
        String title = "System Update";
        String message = "Recent changes: " + updateDetails;
        sendNotification(user, NotificationType.SYSTEM_UPDATE, title, message);
    }

    /**
     * Notification personnalisee
     * @param user User
     * @param title Titre
     * @param message Message
     */
    public void notifyCustom(User user, String title, String message) {
        sendNotification(user, NotificationType.CUSTOM, title, message);
    }

    // === Inbox Helpers ===

    /**
     * Recupere toutes les notifications du user
     * tier du plus recent au plus ancien
     * @param user User
     * @return
     */
    public List<Notification> getAllNotifications(User user) {
        if (user == null || user.getInbox() == null) return new ArrayList<>();
        return reverseCopy(user.getInbox());
    }

    /**
     * Recupere les notifications non lues du user
     * @param user User
     * @return
     */
    public List<Notification> getUnreadNotifications(User user) {
        if (user == null || user.getInbox() == null) return new ArrayList<>();
        return user.getInbox().stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    /**
     * Marque toutes les notifications comme lues
     * @param user User
     */
    public void markAllAsRead(User user) {
        if (user == null || user.getInbox() == null) return;
        user.getInbox().forEach(Notification::markAsRead);
    }

    /**
     * Vide le inbox du user
     * @param user User
     */
    public void clearInbox(User user) {
        if (user == null || user.getInbox() == null) return;
        user.getInbox().clear();
    }

    /**
     * Copie inversee de la liste de notifications
     * par date decroissante
     * @param list
     * @return
     */
    private List<Notification> reverseCopy(List<Notification> list) {
        List<Notification> reversed = new ArrayList<>(list);
        reversed.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return reversed;
    }
}
