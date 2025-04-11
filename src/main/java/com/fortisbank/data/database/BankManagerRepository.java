package com.fortisbank.data.database;

import com.fortisbank.data.dal_utils.DatabaseConnection;
import com.fortisbank.data.interfaces.IBankManagerRepository;
import com.fortisbank.contracts.exceptions.BankManagerRepositoryException;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;
import com.fortisbank.contracts.exceptions.ManagerNotFoundException;
import com.fortisbank.contracts.collections.ManagerList;
import com.fortisbank.contracts.models.users.BankManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for managing bank manager data in the database.
 * Implements the IBankManagerRepository interface.
 */
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
        String query = "SELECT * FROM managers WHERE ManagerID = ?";
        return executeQuery(query, stmt -> stmt.setString(1, managerId), rs -> {
            if (rs.next()) {
                return mapResultSetToManager(rs);
            } else {
                throw new ManagerNotFoundException("Manager with ID " + managerId + " not found.");
            }
        }, "Error retrieving manager with ID: " + managerId);
    }

    @Override
    public void insertManager(BankManager manager) throws BankManagerRepositoryException {
        String query = "INSERT INTO managers (ManagerID, FirstName, LastName, Email, PINHash, PasswordHash) VALUES (?, ?, ?, ?, ?, ?)";
        executeUpdate(query, stmt -> {
            stmt.setString(1, manager.getUserId());
            stmt.setString(2, manager.getFirstName());
            stmt.setString(3, manager.getLastName());
            stmt.setString(4, manager.getEmail());
            stmt.setString(5, manager.getPINHash());
            stmt.setString(6, manager.getHashedPassword());
        }, "Error inserting manager");
    }

    @Override
    public void updateManager(BankManager manager) throws BankManagerRepositoryException {
        String query = "UPDATE managers SET FirstName = ?, LastName = ?, Email = ?, PINHash = ?, PasswordHash = ? WHERE ManagerID = ?";
        executeUpdate(query, stmt -> {
            stmt.setString(1, manager.getFirstName());
            stmt.setString(2, manager.getLastName());
            stmt.setString(3, manager.getEmail());
            stmt.setString(4, manager.getPINHash());
            stmt.setString(5, manager.getHashedPassword());
            stmt.setString(6, manager.getUserId());
        }, "Error updating manager");
    }

    @Override
    public void deleteManager(String managerId) throws BankManagerRepositoryException {
        String query = "DELETE FROM managers WHERE ManagerID = ?";
        executeUpdate(query, stmt -> stmt.setString(1, managerId), "Error deleting manager with ID: " + managerId);
    }

    @Override
    public ManagerList getAllManagers() throws BankManagerRepositoryException {
        String query = "SELECT * FROM managers";
        return executeQuery(query, stmt -> {}, rs -> {
            ManagerList managers = new ManagerList();
            while (rs.next()) {
                managers.add(mapResultSetToManager(rs));
            }
            return managers;
        }, "Error retrieving all managers");
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

    private <T> T executeQuery(String query, QueryPreparer preparer, ResultSetMapper<T> mapper, String errorMessage) throws BankManagerRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.map(rs);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new BankManagerRepositoryException(errorMessage, e);
        }
    }

    private void executeUpdate(String query, QueryPreparer preparer, String errorMessage) throws BankManagerRepositoryException {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.prepare(stmt);
            stmt.executeUpdate();
        } catch (SQLException | DatabaseConnectionException e) {
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new BankManagerRepositoryException(errorMessage, e);
        }
    }

    @FunctionalInterface
    private interface QueryPreparer {
        void prepare(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}