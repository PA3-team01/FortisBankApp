package com.fortisbank.data.file;

import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.collections.NotificationList;
import com.fortisbank.data.dal_utils.NotificationRepositoryException;
import com.fortisbank.data.interfaces.INotificationRepository;

import java.io.File;
import java.util.List;

/**
 * Repository class for managing notifications stored in a file.
 * Extends FileRepository and implements INotificationRepository.
 */
public class NotificationRepositoryFile extends FileRepository<Notification> implements INotificationRepository {

    private static final File file = new File("data/notifications.ser");
    private static NotificationRepositoryFile instance;

    private NotificationRepositoryFile() {
        super(file);
    }

    public static synchronized NotificationRepositoryFile getInstance() {
        if (instance == null) {
            instance = new NotificationRepositoryFile();
        }
        return instance;
    }

    @Override
    public void insertNotification(Notification notification) throws NotificationRepositoryException {
        try {
            List<Notification> notifications = readAll();
            notifications.add(notification);
            writeAll(notifications);
        } catch (Exception e) {
            throw new NotificationRepositoryException("Error inserting notification", e);
        }
    }

    @Override
    public void deleteNotification(String notificationId) throws NotificationRepositoryException {
        try {
            List<Notification> notifications = readAll();
            boolean removed = notifications.removeIf(n -> n.getNotificationId().equals(notificationId));
            if (!removed) {
                throw new NotificationRepositoryException("Notification with ID " + notificationId + " not found for deletion.");
            }
            writeAll(notifications);
        } catch (Exception e) {
            throw new NotificationRepositoryException("Error deleting notification with ID: " + notificationId, e);
        }
    }

    @Override
    public void markAsSeen(String notificationId) throws NotificationRepositoryException {
        try {
            List<Notification> notifications = readAll();
            boolean found = false;
            for (Notification n : notifications) {
                if (n.getNotificationId().equals(notificationId)) {
                    n.setRead(true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new NotificationRepositoryException("Notification with ID " + notificationId + " not found to mark as seen.");
            }
            writeAll(notifications);
        } catch (Exception e) {
            throw new NotificationRepositoryException("Error updating notification", e);
        }
    }

    @Override
    public NotificationList getNotificationsByUserId(String userId) throws NotificationRepositoryException {
        try {
            List<Notification> result = readAll().stream()
                    .filter(n -> userId.equals(n.getRecipientUserId()))
                    .toList();
            return new NotificationList(result);
        } catch (Exception e) {
            throw new NotificationRepositoryException("Error retrieving notifications for user: " + userId, e);
        }
    }



    @Override
    public Notification getNotificationById(String id) throws NotificationRepositoryException {
        try {
            return readAll().stream()
                    .filter(n -> n.getNotificationId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotificationRepositoryException("Notification with ID " + id + " not found."));
        } catch (Exception e) {
            throw new NotificationRepositoryException("Error retrieving notification with ID: " + id, e);
        }
    }
}
