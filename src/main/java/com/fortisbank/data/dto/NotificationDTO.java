package com.fortisbank.data.dto;

import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.models.others.NotificationType;

import java.util.Date;

/**
 * DTO (Data Transfer Object) for Notification.
 * This class is used to transfer notification data between layers of the application.
 * It includes validation and conversion methods to and from the Notification entity.
 */

public record NotificationDTO(
        String notificationId,
        String recipientUserId,
        String accountId,
        String type,
        String title,
        String message,
        boolean seen,
        Date timestamp
) {
    public NotificationDTO {
        // Field validation
        if (notificationId == null || notificationId.isBlank())
            throw new IllegalArgumentException("Notification ID cannot be null or blank.");
        if (recipientUserId == null || recipientUserId.isBlank())
            throw new IllegalArgumentException("Recipient User ID cannot be null or blank.");
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Notification type cannot be null or blank.");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be null or blank.");
        if (message == null)
            throw new IllegalArgumentException("Message cannot be null.");
        if (timestamp == null)
            throw new IllegalArgumentException("Timestamp cannot be null.");
    }

    /**
     * Maps a Notification entity to a DTO.
     *
     * @param n the Notification object
     * @return NotificationDTO
     */
    public static NotificationDTO fromEntity(Notification n) {
        if (n == null) throw new IllegalArgumentException("Notification entity cannot be null.");

        return new NotificationDTO(
                n.getNotificationId(),
                n.getRecipientUserId(),
                n.getRelatedAccount() != null ? n.getRelatedAccount().getAccountNumber() : null,
                n.getType().name(),
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getTimestamp()
        );
    }

    /**
     * Converts this DTO back into a Notification entity.
     *
     * @return Notification object
     */
    public Notification toEntity() {
        NotificationType parsedType;
        try {
            parsedType = NotificationType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalStateException("Invalid or null NotificationType: " + type);
        }

        return new Notification(
                notificationId,
                recipientUserId,
                accountId,
                parsedType,
                title,
                message,
                seen,
                timestamp
        );
    }

}
