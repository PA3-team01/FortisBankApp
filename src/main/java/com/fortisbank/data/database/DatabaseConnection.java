package com.fortisbank.data.database;
// TODO: Singleton pattern
public class DatabaseConnection implements IDatabaseConnection {
    private final String _connectionString = "";
    private final Object _connection = null;

    public DatabaseConnection(String connectionString) {}

    public DatabaseConnection() {}

    @Override
    public void Connect() { //TODO: Implement this method
        //if (!TestConnection()) return null; // if connection is not valid return null
        // create connection object
        // return connection object
        //return _connection;
    }

    @Override
    public void Disconnect() { //TODO: Implement this method

    }

    // test connection
    public boolean TestConnection() { //TODO: Implement this method
        // if is valid connection
        return true;
    }
}
