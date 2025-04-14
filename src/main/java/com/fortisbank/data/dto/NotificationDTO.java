package com.fortisbank.data.dto;

import com.fortisbank.business.services.account.AccountService;
import com.fortisbank.business.services.users.customer.CustomerService;
import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.models.others.NotificationType;
import com.fortisbank.data.dal_utils.StorageMode;

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
        Date timestamp,
        String relatedCustomerId
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
                n.getTimestamp(),
                n.getRelatedCustomer() != null ? n.getRelatedCustomer().getUserId() : null
        );
    }

    /**
     * Converts this DTO back into a Notification entity.
     *
     * @return Notification object
     */
    public Notification toEntity() {
        NotificationType parsedType = NotificationType.valueOf(type);

        Notification notification = new Notification(
                notificationId,
                recipientUserId,
                parsedType,
                title,
                message,
                seen,
                timestamp
        );

        if (accountId != null) {
            try {
                notification.setRelatedAccount(AccountService.getInstance(StorageMode.DATABASE).getAccount(accountId));
            } catch (Exception e) {
                // Log but allow fallback
                System.err.println("Failed to load related account: " + e.getMessage()); //TODO: replace with proper logging
            }
        }

        if (relatedCustomerId != null) {
            try {
                notification.setRelatedCustomer(CustomerService.getInstance(StorageMode.DATABASE).getCustomer(relatedCustomerId));
            } catch (Exception e) {
                // Log but allow fallback (e.g., for managers)
                System.err.println("No customer found for relatedCustomerId: " + relatedCustomerId); //TODO: replace with proper logging
            }
        }

        return notification;
    }



}
