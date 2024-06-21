package org.example.persistance.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnector {
    public static Connection connect(String hostPort) throws SQLException {
        try {
            // Get database credentials from DatabaseConfig class
            String jdbcUrl = DatabaseConfig.getDbUrlDriver() + hostPort + DatabaseConfig.getDbName();
            String user = DatabaseConfig.getDbUsername();
            String password = DatabaseConfig.getDbPassword();


            // Open a connection
            return DriverManager.getConnection(jdbcUrl, user, password);

        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
