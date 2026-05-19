package org.agenda.shared.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Connection connection;

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 3_000;

    private static final String DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Madrid",
            getEnv("DB_HOST", "localhost"),
            getEnv("DB_PORT", "3306"),
            getEnv("DB_NAME", "agenda")
    );
    private static final String DB_USER = getEnv("DB_USER", "root");
    private static final String DB_PASS = getEnv("DB_PASS", "password");

    private DatabaseConnection() throws SQLException {
        this.connection = establishConnectionWithRetry();
    }

    private Connection establishConnectionWithRetry() throws SQLException {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.printf("[DB] Attempt %d/%d — Connecting to %s%n",
                        attempt, MAX_RETRIES, DB_URL);
                return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            } catch (SQLException e) {
                lastException = e;
                System.err.printf("[DB] Failed on attempt %d: %s%n",
                        attempt, e.getMessage());

                if (attempt < MAX_RETRIES) {
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
                "Critical: Database unreachable after " + MAX_RETRIES + " attempts.",
                lastException
        );
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}