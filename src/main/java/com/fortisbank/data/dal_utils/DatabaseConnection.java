package com.fortisbank.data.dal_utils;

import com.fortisbank.data.interfaces.IDatabaseConnection;
import com.fortisbank.contracts.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection implements IDatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;

    private final String connectionString = "jdbc:oracle:thin:@//aedev.pro:1521/XEPDB1";
    private final String username = "java_course";
    private final String password = "Baddemon665";

    private DatabaseConnection() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Oracle JDBC Driver not found.", e);
            throw new RuntimeException("Oracle JDBC Driver not found.", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws DatabaseConnectionException {
        try {
            Connection conn = DriverManager.getConnection(connectionString, username, password);
            LOGGER.log(Level.INFO, "Connected to Oracle Database successfully.");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the database.", e);
            throw new DatabaseConnectionException("Failed to connect to the database.", e);
        }
    }

    @Override
    public boolean TestConnection() {
        try (Connection testConn = DriverManager.getConnection(connectionString, username, password)) {
            LOGGER.log(Level.INFO, "Database connection test successful.");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed.", e);
            return false;
        }
    }
}
