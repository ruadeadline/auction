package com.uet.auction.server.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/auction_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL Driver!", e);
        }
    }
}