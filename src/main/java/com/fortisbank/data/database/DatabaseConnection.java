package com.fortisbank.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * gere la connexion/ deconnexion
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
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Etablit la connexion a la base de donnees Oracle
     */
    // Connect to Oracle Database
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
     * Ferme la connexion
     */
    // Disconnect from Database
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
     * Teste la connexion a la database
     * @return true su la connexion est reussi
     */
    // Test Database Connection
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

    // Get Connection (Optional for external use)
    public Connection getConnection() {
        return connection;
    }
}
