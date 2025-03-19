package com.fortisbank.data.database;

public interface IDatabaseConnection {

    String _connectionString = "";
    Object _connection = null;

    public void Connect();
    public void Disconnect();
}
