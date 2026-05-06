package org.agenda.shared.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_HOST = getEnv("DB_HOST", "localhost");
    private static final String DB_PORT = getEnv("DB_PORT", "3306");
    private static final String DB_NAME = getEnv("DB_NAME", "cli_agenda");
    private static final String DB_USER = getEnv("DB_USER", "agenda_user");
    private static final String DB_PASS = getEnv("DB_PASS", "agenda_pass");

    private static final String JDBC_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            DB_HOST, DB_PORT, DB_NAME
    );

    private static final int MAX_RETRIES = 5;

    private static final long RETRY_DELAY_MS = 3_000;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {

                System.out.printf("[DB] Attempt %d/%d — connecting to %s%n", attempt, MAX_RETRIES, JDBC_URL);
                Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
                System.out.println("[DB] Connection established successfully.");
                return connection;

            } catch (SQLException e) {
                lastException = e;
                System.err.printf("[DB] Failed on attempt %d: %s%n", attempt, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    System.out.printf("[DB] Retrying in %d seconds...%n", RETRY_DELAY_MS / 1000);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        throw new SQLException(
                String.format("[DB] Could not connect to MySQL after %d attempts. URL: %s", MAX_RETRIES, JDBC_URL),
                lastException
        );
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
