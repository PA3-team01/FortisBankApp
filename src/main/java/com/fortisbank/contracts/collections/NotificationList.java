package com.fortisbank.contracts.collections;

import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.contracts.models.others.NotificationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * A list for managing notifications (filtering, formatting).
 */
public class NotificationList extends ArrayList<Notification> {

    /**
     * Default constructor initializing an empty notification list.
     */
    public NotificationList() {
        super();
    }

    /**
     * Constructor initializing the list with an iterable collection.
     *
     * @param notifications iterable list of notifications to add
     */
    public NotificationList(Iterable<Notification> notifications) {
        notifications.forEach(this::add);
    }

    /**
     * Filters the list by unseen notifications.
     *
     * @return a list containing only unseen notifications
     */
    public NotificationList filterUnseen() {
        return this.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toCollection(NotificationList::new));
    }

    /**
     * Filters notifications by type.
     *
     * @param type the notification type
     * @return filtered list of matching notifications
     */
    public NotificationList filterByType(NotificationType type) {
        return this.stream()
                .filter(n -> type.equals(n.getType()))
                .collect(Collectors.toCollection(NotificationList::new));
    }

    /**
     * Filters notifications created after a specific date-time.
     *
     * @param after the date-time to filter after
     * @return list of notifications created after the specified time
     */
    public NotificationList filterAfter(Date after) {
        return new NotificationList(
                this.stream()
                        .filter(n -> n.getTimestamp().after(after))
                        .toList()
        );
    }


    /**
     * Returns a human-readable string representation of the list.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationList: ").append(this.size()).append(" notifications\n");
        for (Notification notification : this) {
            builder.append("  - ").append(notification.toString()).append("\n");
        }
        return builder.toString();
    }
}
