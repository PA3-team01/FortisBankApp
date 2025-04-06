package com.fortisbank.data.database;

import java.sql.Connection;

/**
 * Interface for managing database connections.
 */
public interface IDatabaseConnection {

    /**
     * Establishes the connection to the database.
     */
    void Connect();

    /**
     * Closes the connection to the database.
     */
    void Disconnect();

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
    Connection getConnection();
}