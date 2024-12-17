package utils;

import java.sql.*;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/votify";
    private static final String USER = "root";
    private static final String PASSWORD = "1473982111";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                Statement statement = connection.createStatement();

                // User Table
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "username VARCHAR(50) UNIQUE,"
                        + "password VARCHAR(50),"
                        + "is_active BOOLEAN DEFAULT TRUE)");

                // Poll Table
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS polls ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "title VARCHAR(100),"
                        + "start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                        + "end_date TIMESTAMP NULL)");

                // Votes Table
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS votes ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "user_id INT,"
                        + "poll_id INT,"
                        + "choice VARCHAR(50),"
                        + "FOREIGN KEY (user_id) REFERENCES users(id),"
                        + "FOREIGN KEY (poll_id) REFERENCES polls(id))");

                System.out.println("Database initialized successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
