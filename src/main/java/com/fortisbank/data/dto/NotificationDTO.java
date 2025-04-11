package com.fortisbank.data.dto;

import java.util.Date;

public record NotificationDTO(
        String notificationId,
        String userId,              // FK to users (Customer or Manager)
        String accountId,           // Nullable FK to accounts
        String type,
        String title,
        String message,
        boolean seen,
        Date timestamp
) {}
