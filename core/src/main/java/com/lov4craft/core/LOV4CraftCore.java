package com.lov4craft.core;

import com.lov4craft.core.config.ConfigManager;
import com.lov4craft.core.database.DatabaseManager;
import com.lov4craft.core.commands.LOV4CraftCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LOV4CraftCore extends JavaPlugin {
    
    @Getter
    private static LOV4CraftCore instance;
    
    @Getter
    private ConfigManager configManager;
    
    @Getter
    private DatabaseManager databaseManager;
    
    @Getter
    private JedisPool redisPool;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        // Initialize database connection
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Initialize Redis connection
        initializeRedis();

        // Register commands
        registerCommands();

        // Register event listeners
        registerListeners();

        getLogger().info("LOV4CRAFT Core has been enabled!");
    }

    @Override
    public void onDisable() {
        // Close database connections
        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        // Close Redis connections
        if (redisPool != null && !redisPool.isClosed()) {
            redisPool.close();
        }

        getLogger().info("LOV4CRAFT Core has been disabled!");
    }

    private void initializeRedis() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);

        String redisHost = getConfig().getString("redis.host", "localhost");
        int redisPort = getConfig().getInt("redis.port", 6379);
        String redisPassword = getConfig().getString("redis.password", null);

        if (redisPassword != null && redisPassword.isEmpty()) {
            redisPassword = null;
        }

        redisPool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
    }

    private void registerCommands() {
        getCommand("lov4craft").setExecutor(new LOV4CraftCommand(this));
    }

    private void registerListeners() {
        // Register event listeners here
    }

    /**
     * Reloads all configurations and reconnects to services
     */
    public void reload() {
        reloadConfig();
        configManager.loadConfigs();
        
        // Reconnect to database
        databaseManager.shutdown();
        databaseManager.initialize();
        
        // Reconnect to Redis
        if (redisPool != null && !redisPool.isClosed()) {
            redisPool.close();
        }
        initializeRedis();
    }
}
