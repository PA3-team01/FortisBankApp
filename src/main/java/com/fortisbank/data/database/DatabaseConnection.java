package com.fortisbank.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection and disconnection to the database.
 */
public class DatabaseConnection implements IDatabaseConnection {
    private static DatabaseConnection instance; // Singleton instance
    private Connection connection;
    private final String connectionString = "jdbc:oracle:thin:@localhost:1521:xe"; // TODO: Change this
    private final String username = "your_username"; // TODO: Change this
    private final String password = "your_password"; // TODO: Change this

    // Private constructor to prevent direct instantiation
    private DatabaseConnection() {
    }

    // Singleton Instance Method
    /**
     * Returns the singleton instance of DatabaseConnection.
     *
     * @return the singleton instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Establishes the connection to the Oracle database.
     */
    @Override
    public void Connect() {
        if (connection != null) {
            System.out.println("Already connected to the database."); //TODO: use logger
            return;
        }

        try {
            // Load the Oracle JDBC Driver (Not needed for Java 6+ but good practice)
            Class.forName("oracle.jdbc.OracleDriver");

            // Establish connection
            connection = DriverManager.getConnection(connectionString, username, password);
            System.out.println("Connected to Oracle Database successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection to the database.
     */
    @Override
    public void Disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.err.println("Error while disconnecting.");
            e.printStackTrace();
        }
    }

    /**
     * Tests the connection to the database.
     *
     * @return true if the connection is successful, false otherwise
     */
    public boolean TestConnection() {
        try (Connection testConn = DriverManager.getConnection(connectionString, username, password)) {
            System.out.println("Database connection test successful.");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the current database connection.
     *
     * @return the current database connection
     */
    public Connection getConnection() {
        return connection;
    }
}