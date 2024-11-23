package com.lov4craft.core.database;

import com.lov4craft.core.LOV4CraftCore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private final LOV4CraftCore plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(LOV4CraftCore plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        FileConfiguration config = plugin.getConfigManager().getConfig("database.yml");
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
                config.getString("database.host", "localhost"),
                config.getInt("database.port", 3306),
                config.getString("database.name", "lov4craft")));
        hikariConfig.setUsername(config.getString("database.username", "root"));
        hikariConfig.setPassword(config.getString("database.password", ""));
        
        // HikariCP settings
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setMaxLifetime(600000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        
        try {
            dataSource = new HikariDataSource(hikariConfig);
            createTables();
            plugin.getLogger().info("Database connection established successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database connection!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection has not been initialized!");
        }
        return dataSource.getConnection();
    }

    private void createTables() {
        try (Connection conn = getConnection()) {
            // Create necessary tables
            String[] tables = {
                // Players table
                """
                CREATE TABLE IF NOT EXISTS players (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    uuid VARCHAR(36) UNIQUE NOT NULL,
                    username VARCHAR(16) NOT NULL,
                    wallet_address VARCHAR(42),
                    paypal_email VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """,
                // Couples table
                """
                CREATE TABLE IF NOT EXISTS couples (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    player1_id BIGINT NOT NULL,
                    player2_id BIGINT NOT NULL,
                    status ENUM('pending', 'active', 'inactive') DEFAULT 'pending',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (player1_id) REFERENCES players(id),
                    FOREIGN KEY (player2_id) REFERENCES players(id)
                )
                """,
                // Transactions table
                """
                CREATE TABLE IF NOT EXISTS transactions (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    player_id BIGINT NOT NULL,
                    type ENUM('reward', 'purchase', 'withdrawal') NOT NULL,
                    amount DECIMAL(18,8) NOT NULL,
                    currency VARCHAR(10) NOT NULL,
                    tx_hash VARCHAR(66),
                    status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (player_id) REFERENCES players(id)
                )
                """
            };

            for (String table : tables) {
                conn.createStatement().execute(table);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create database tables!");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
