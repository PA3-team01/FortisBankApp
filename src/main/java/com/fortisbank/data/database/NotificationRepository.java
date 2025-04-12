package com.fortisbank.data.database;

import com.fortisbank.contracts.collections.NotificationList;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.models.others.Notification;
import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.dal_utils.NotificationRepositoryException;
import com.fortisbank.data.dto.NotificationDTO;
import com.fortisbank.data.interfaces.INotificationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository implements INotificationRepository {

    private final Connection conn;
    private static NotificationRepository instance;

    private NotificationRepository() throws DatabaseConnectionException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            try {
                instance = new NotificationRepository();
            } catch (DatabaseConnectionException e) {
                throw new RuntimeException("Failed to initialize NotificationRepository", e);
            }
        }
        return instance;
    }

    @Override
    public void insertNotification(Notification notification) throws NotificationRepositoryException {
        NotificationDTO dto = NotificationDTO.fromEntity(notification);

        String sql = "INSERT INTO notifications (notification_id, recipient_user_id, account_id, title, message, type, seen, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.notificationId());
            stmt.setString(2, dto.recipientUserId());
            stmt.setString(3, dto.accountId());
            stmt.setString(4, dto.title());
            stmt.setString(5, dto.message());
            stmt.setString(6, dto.type());
            stmt.setInt(7, dto.seen() ? 1 : 0);
            stmt.setTimestamp(8, new Timestamp(dto.timestamp().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new NotificationRepositoryException("Failed to insert notification", e);
        }
    }

    @Override
    public void deleteNotification(String notificationId) throws NotificationRepositoryException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new NotificationRepositoryException("Failed to delete notification", e);
        }
    }

    @Override
    public void markAsSeen(String notificationId) throws NotificationRepositoryException {
        String sql = "UPDATE notifications SET seen = 1 WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new NotificationRepositoryException("Failed to mark notification as seen", e);
        }
    }

    @Override
    public NotificationList getNotificationsByUserId(String userId) throws NotificationRepositoryException {
        String sql = "SELECT n.*, a.account_type FROM notifications n " +
                "LEFT JOIN accounts a ON n.account_id = a.account_id " +
                "WHERE n.recipient_user_id = ? ORDER BY n.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            List<Notification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(mapResultSetToDTO(rs).toEntity());
            }
            return new NotificationList(notifications);
        } catch (SQLException e) {
            throw new NotificationRepositoryException("Failed to retrieve notifications for user: " + userId, e);
        }
    }

    @Override
    public Notification getNotificationById(String id) throws NotificationRepositoryException {
        String sql = "SELECT n.*, a.account_type FROM notifications n " +
                "LEFT JOIN accounts a ON n.account_id = a.account_id " +
                "WHERE n.notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDTO(rs).toEntity();
            } else {
                throw new NotificationRepositoryException("Notification with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new NotificationRepositoryException("Failed to retrieve notification with ID: " + id, e);
        }
    }

    private NotificationDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new NotificationDTO(
                rs.getString("notification_id"),
                rs.getString("recipient_user_id"),
                rs.getString("account_id"),
                rs.getString("type"),
                rs.getString("title"),
                rs.getString("message"),
                rs.getInt("seen") == 1,
                rs.getTimestamp("created_at")
        );
    }
}
