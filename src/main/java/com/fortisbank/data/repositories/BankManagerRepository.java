package com.fortisbank.data.repositories;

import com.fortisbank.data.database.DatabaseConnection;
import com.fortisbank.exceptions.ManagerNotFoundException;
import com.fortisbank.models.collections.ManagerList;
import com.fortisbank.models.users.BankManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static com.fortisbank.data.repositories.AccountRepository.LOGGER;

public class BankManagerRepository implements IBankManagerRepository {

    private static BankManagerRepository instance;
    private final DatabaseConnection dbConnection;

    private BankManagerRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public static synchronized BankManagerRepository getInstance() {
        if (instance == null) {
            instance = new BankManagerRepository();
        }
        return instance;
    }

    @Override
    public BankManager getManagerById(String managerId) {
        String query = "SELECT * FROM managers WHERE ManagerID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, managerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToManager(rs);
            } else {
                throw new ManagerNotFoundException("Manager with ID " + managerId + " not found.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving manager: {0}", e.getMessage());
            throw new ManagerNotFoundException("Error retrieving manager " + managerId);
        }
    }

    @Override
    public void insertManager(BankManager manager) {
        String query = "INSERT INTO managers (ManagerID, FirstName, LastName, Email, PINHash, PasswordHash) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, manager.getUserId());
            stmt.setString(2, manager.getFirstName());
            stmt.setString(3, manager.getLastName());
            stmt.setString(4, manager.getEmail());
            stmt.setString(5, manager.getPINHash());
            stmt.setString(6, manager.getHashedPassword());
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting manager: {0}", e.getMessage());
        }
    }

    @Override
    public void updateManager(BankManager manager) {
        String query = "UPDATE managers SET FirstName = ?, LastName = ?, Email = ?, PINHash = ?, PasswordHash = ? WHERE ManagerID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, manager.getFirstName());
            stmt.setString(2, manager.getLastName());
            stmt.setString(3, manager.getEmail());
            stmt.setString(4, manager.getPINHash());
            stmt.setString(5, manager.getHashedPassword());
            stmt.setString(6, manager.getUserId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating manager: {0}", e.getMessage());
        }
    }

    @Override
    public void deleteManager(String managerId) {
        String query = "DELETE FROM managers WHERE ManagerID = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, managerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting manager: {0}", e.getMessage());
        }
    }

    @Override
    public ManagerList getAllManagers() {
        ManagerList managers = new ManagerList();
        String query = "SELECT * FROM managers";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                managers.add(mapResultSetToManager(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving managers: {0}", e.getMessage());
        }
        return managers;
    }

    private BankManager mapResultSetToManager(ResultSet rs) throws SQLException {
        BankManager manager = new BankManager();
        manager.setUserId(rs.getString("ManagerID"));
        manager.setFirstName(rs.getString("FirstName"));
        manager.setLastName(rs.getString("LastName"));
        manager.setEmail(rs.getString("Email"));
        manager.setPINHash(rs.getString("PINHash"));
        manager.setHashedPassword(rs.getString("PasswordHash"));
        return manager;
    }
}
