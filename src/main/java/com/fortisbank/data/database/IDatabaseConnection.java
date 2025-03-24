package com.fortisbank.data.database;

import java.sql.Connection;

public interface IDatabaseConnection {

    // Methods to be implemented by DatabaseConnection
    void Connect();

    void Disconnect();

    boolean TestConnection();

    Connection getConnection();
}
