package edu.vcet.canteen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv().getOrDefault("DB_URL",
            "jdbc:postgresql://localhost:5432/canteen_db");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException("DB_PASSWORD is not set. Set your PostgreSQL password first.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
