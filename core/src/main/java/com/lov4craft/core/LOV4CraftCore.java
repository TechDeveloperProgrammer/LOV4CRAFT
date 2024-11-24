package com.lov4craft.core;

import com.lov4craft.core.config.ConfigManager;
import com.lov4craft.core.database.DatabaseManager;
import com.lov4craft.core.commands.*;
import com.lov4craft.core.ai.AIManager;
import com.lov4craft.core.ai.config.AIConfig;
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
    
    @Getter
    private AIManager aiManager;
    
    @Getter
    private AIConfig aiConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        // Initialize AI configuration and manager
        aiConfig = new AIConfig(this);
        aiManager = new AIManager(this, aiConfig);

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
        
        // Log AI services status
        aiManager.logServicesStatus();
    }

    @Override
    public void onDisable() {
        // Shutdown AI services
        if (aiManager != null) {
            aiManager.shutdown();
        }

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
        // Register main plugin command
        getCommand("lov4craft").setExecutor(new LOV4CraftCommand(this));
        
        // Register couple commands
        CoupleCommand coupleCommand = new CoupleCommand(this);
        getCommand("couple").setExecutor(coupleCommand);
        getCommand("couple").setTabCompleter(coupleCommand);
        
        // Register mission commands
        MissionCommand missionCommand = new MissionCommand(this);
        getCommand("mission").setExecutor(missionCommand);
        getCommand("mission").setTabCompleter(missionCommand);
        
        // Register wallet commands
        WalletCommand walletCommand = new WalletCommand(this);
        getCommand("wallet").setExecutor(walletCommand);
        getCommand("wallet").setTabCompleter(walletCommand);
    }

    private void registerListeners() {
        // TODO: Register event listeners
        // getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        // getServer().getPluginManager().registerEvents(new MissionListener(this), this);
        // getServer().getPluginManager().registerEvents(new CoupleListener(this), this);
    }

    /**
     * Reloads all configurations and reconnects to services
     */
    public void reload() {
        reloadConfig();
        configManager.loadConfigs();
        
        // Reload AI configuration and services
        aiConfig = new AIConfig(this);
        aiManager.reload();
        
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
