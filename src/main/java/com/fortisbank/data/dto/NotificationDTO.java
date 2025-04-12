package com.fortisbank.data.dto;

import com.fortisbank.contracts.models.others.Notification;

import java.util.Date;

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
    /**
     * Maps a Notification object to a NotificationDTO.
     *
     * @param n the Notification to map
     * @return NotificationDTO
     */
    public static NotificationDTO fromEntity(Notification n) {
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
     * Maps a NotificationDTO to a Notification object.
     *
     * @return Notification
     */
    public Notification toEntity() {
        return new Notification(
                notificationId,
                recipientUserId,
                accountId,
                type,
                title,
                message,
                seen,
                timestamp
        );
    }
}
