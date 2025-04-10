package com.fortisbank.data.interfaces;

import com.fortisbank.contracts.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for managing database connections.
 */
public interface IDatabaseConnection {

    /**
     * Tests the connection to the database.
     *
     * @return true if the connection is successful, false otherwise
     */
    boolean TestConnection();

    /**
     * Returns the current database connection.
     *
     * @return the current database connection
     */
    Connection getConnection() throws SQLException, DatabaseConnectionException;
}