package com.lov4craft.core.config;

import com.lov4craft.core.LOV4CraftCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final LOV4CraftCore plugin;
    private final Map<String, FileConfiguration> configs;

    public ConfigManager(LOV4CraftCore plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
    }

    public void loadConfigs() {
        // Load default configuration files
        loadConfig("database.yml");
        loadConfig("redis.yml");
        loadConfig("couples.yml");
        loadConfig("missions.yml");
        loadConfig("crypto.yml");
    }

    private void loadConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        FileConfiguration config;

        if (!configFile.exists()) {
            // Try to load default config from resources
            InputStream defaultStream = plugin.getResource(fileName);
            if (defaultStream != null) {
                try {
                    // Save default config to file
                    plugin.saveResource(fileName, false);
                    // Load the saved config
                    config = YamlConfiguration.loadConfiguration(configFile);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Could not save default config for " + fileName);
                    // Load from stream as fallback
                    config = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                    );
                }
            } else {
                // Create empty config if no default exists
                config = new YamlConfiguration();
                plugin.getLogger().warning("No default config found for " + fileName + ", creating empty configuration");
            }
        } else {
            // Load existing config
            config = YamlConfiguration.loadConfiguration(configFile);
            
            // Check for updates in default config
            InputStream defaultStream = plugin.getResource(fileName);
            if (defaultStream != null) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defaultConfig);
                config.options().copyDefaults(true);
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not save updated config for " + fileName);
                    e.printStackTrace();
                }
            }
        }

        configs.put(fileName, config);
    }

    public FileConfiguration getConfig(String fileName) {
        FileConfiguration config = configs.get(fileName);
        if (config == null) {
            loadConfig(fileName);
            config = configs.get(fileName);
        }
        return config;
    }

    public void saveConfig(String fileName) {
        FileConfiguration config = configs.get(fileName);
        if (config == null) return;

        try {
            config.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config file " + fileName);
            e.printStackTrace();
        }
    }

    public void reloadConfig(String fileName) {
        configs.remove(fileName);
        loadConfig(fileName);
    }

    public void reloadAllConfigs() {
        configs.clear();
        loadConfigs();
    }

    public void saveDefaultConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            InputStream defaultStream = plugin.getResource(fileName);
            if (defaultStream != null) {
                plugin.saveResource(fileName, false);
            } else {
                plugin.getLogger().warning("No default config found for " + fileName);
            }
        }
    }
}
