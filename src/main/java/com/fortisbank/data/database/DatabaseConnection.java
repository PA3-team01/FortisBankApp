package com.fortisbank.data.database;

    import com.fortisbank.data.interfaces.IDatabaseConnection;
    import com.fortisbank.contracts.exceptions.DatabaseConnectionException;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    /**
     * Manages the connection and disconnection to the database.
     */
    public class DatabaseConnection implements IDatabaseConnection {
        private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
        private static final int MAX_RETRIES = 3; // Retry limit
        private static DatabaseConnection instance;
        private Connection connection;
        private final String connectionString = "jdbc:oracle:thin:@localhost:1521:xe"; // TODO: Change this
        private final String username = "your_username"; // TODO: Change this
        private final String password = "your_password"; // TODO: Change this

        private DatabaseConnection() {
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
            if (connection == null || isConnectionClosed()) {
                connectWithRetry();
            }
            return connection;
        }

        @Override
        public boolean TestConnection() {
            // This method does not rethrow exceptions because its purpose is to provide a simple
            // boolean result indicating whether the connection test was successful or not.
            // This design avoids burdening the caller with exception handling for a test operation.
            try (Connection testConn = DriverManager.getConnection(connectionString, username, password)) {
                LOGGER.log(Level.INFO, "Database connection test successful.");
                return true;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Database connection test failed.", e);
                return false;
            }
        }

        private void connectWithRetry() throws DatabaseConnectionException {
            int attempts = 0;
            while (attempts < MAX_RETRIES) {
                try {
                    connect();
                    return;
                } catch (SQLException e) {
                    attempts++;
                    LOGGER.log(Level.WARNING, "Connection attempt {0} failed. Retrying...", attempts);
                    if (attempts >= MAX_RETRIES) {
                        LOGGER.log(Level.SEVERE, "All connection attempts failed.", e);
                        throw new DatabaseConnectionException("Failed to connect to the database after " + MAX_RETRIES + " attempts.", e);
                    }
                }
            }
        }

        private void connect() throws SQLException {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                connection = DriverManager.getConnection(connectionString, username, password);
                LOGGER.log(Level.INFO, "Connected to Oracle Database successfully.");
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Oracle JDBC Driver not found.", e);
                throw new SQLException("Oracle JDBC Driver not found.", e);
            }
        }

        private boolean isConnectionClosed() {
            try {
                return connection == null || connection.isClosed();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error checking connection status.", e);
                return true;
            }
        }
    }