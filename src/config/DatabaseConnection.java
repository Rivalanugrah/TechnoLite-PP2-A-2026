package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // MAMP MySQL default port: 8889, password: root
    private static final String URL = "jdbc:mysql://localhost:8889/perpustakaan_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Create new connection each time to avoid stale connection issues
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Gagal koneksi ke database!");
            e.printStackTrace();
        }
        return null;
    }
}
