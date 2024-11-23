package com.lov4craft.core.config;

import com.lov4craft.core.LOV4CraftCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(fileName, config);
    }

    public FileConfiguration getConfig(String fileName) {
        return configs.get(fileName);
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
        loadConfig(fileName);
    }

    public void saveDefaultConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}
