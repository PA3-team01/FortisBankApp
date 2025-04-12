package com.fortisbank.data.database;

import com.fortisbank.contracts.collections.ManagerList;
import com.fortisbank.contracts.exceptions.*;
import com.fortisbank.contracts.models.users.BankManager;
import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.dto.BankManagerDTO;
import com.fortisbank.data.interfaces.IBankManagerRepository;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankManagerRepository implements IBankManagerRepository {

    private static final Logger LOGGER = Logger.getLogger(BankManagerRepository.class.getName());
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
    public BankManager getManagerById(String managerId) throws BankManagerRepositoryException {
        String query = "SELECT * FROM users u JOIN bank_managers bm ON u.user_id = bm.user_id WHERE u.user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, managerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs).toEntity();
            } else {
                throw new ManagerNotFoundException("Manager with ID " + managerId + " not found.");
            }

        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving manager: {0}", e.getMessage());
            throw new BankManagerRepositoryException("Failed to retrieve manager", e);
        }
    }

    @Override
    public ManagerList getAllManagers() throws BankManagerRepositoryException {
        String query = "SELECT * FROM users u JOIN bank_managers bm ON u.user_id = bm.user_id WHERE u.role = 'MANAGER'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ManagerList managers = new ManagerList();
            while (rs.next()) {
                managers.add(mapResultSetToDTO(rs).toEntity());
            }
            return managers;

        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all managers: {0}", e.getMessage());
            throw new BankManagerRepositoryException("Failed to retrieve all managers", e);
        }
    }

    @Override
    public void insertManager(BankManager manager) throws BankManagerRepositoryException {
        BankManagerDTO dto = BankManagerDTO.fromEntity(manager);

        String insertUser = "INSERT INTO users (user_id, first_name, last_name, email, hashed_password, pin_hash, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertManager = "INSERT INTO bank_managers (user_id) VALUES (?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(insertUser);
                 PreparedStatement mgrStmt = conn.prepareStatement(insertManager)) {

                userStmt.setString(1, dto.userId());
                userStmt.setString(2, dto.firstName());
                userStmt.setString(3, dto.lastName());
                userStmt.setString(4, dto.email());
                userStmt.setString(5, dto.hashedPassword());
                userStmt.setString(6, dto.pinHash());
                userStmt.setString(7, "MANAGER");

                mgrStmt.setString(1, dto.userId());

                userStmt.executeUpdate();
                mgrStmt.executeUpdate();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new BankManagerRepositoryException("Failed to insert manager", e);
            }

        } catch (SQLException | DatabaseConnectionException e) {
            throw new BankManagerRepositoryException("Failed to insert manager", e);
        }
    }

    @Override
    public void updateManager(BankManager manager) throws BankManagerRepositoryException {
        BankManagerDTO dto = BankManagerDTO.fromEntity(manager);

        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, hashed_password = ?, pin_hash = ? WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dto.firstName());
            stmt.setString(2, dto.lastName());
            stmt.setString(3, dto.email());
            stmt.setString(4, dto.hashedPassword());
            stmt.setString(5, dto.pinHash());
            stmt.setString(6, dto.userId());

            stmt.executeUpdate();

        } catch (SQLException | DatabaseConnectionException e) {
            throw new BankManagerRepositoryException("Failed to update manager", e);
        }
    }

    @Override
    public void deleteManager(String managerId) throws BankManagerRepositoryException {
        String deleteManager = "DELETE FROM bank_managers WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteManager);
                 PreparedStatement stmt2 = conn.prepareStatement(deleteUser)) {

                stmt1.setString(1, managerId);
                stmt2.setString(1, managerId);

                stmt1.executeUpdate();
                stmt2.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new BankManagerRepositoryException("Failed to delete manager", e);
            }

        } catch (SQLException | DatabaseConnectionException e) {
            throw new BankManagerRepositoryException("Failed to delete manager", e);
        }
    }

    private BankManagerDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new BankManagerDTO(
                rs.getString("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("hashed_password"),
                rs.getString("pin_hash"),
                rs.getString("role")
        );
    }
}
