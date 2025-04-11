package com.fortisbank.data.interfaces;

import com.fortisbank.contracts.collections.NotificationList;
import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.data.dal_utils.NotificationRepositoryException;

public interface INotificationRepository {
    void insertNotification(Notification notification) throws NotificationRepositoryException;
    void deleteNotification(String notificationId) throws NotificationRepositoryException;
    void markAsSeen(String notificationId) throws NotificationRepositoryException;
    NotificationList getNotificationsByUserId(String userId) throws NotificationRepositoryException;
    Notification getNotificationById(String id) throws NotificationRepositoryException;
}
