package com.lov4craft.core.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GlobalConfig {
    private boolean debugMode;
    private int threadPoolSize;
    private long cacheTimeout;
    private double defaultConfidenceThreshold;
    private Map<String, String> apiKeys;
    private PerformanceConfig performanceConfig;
    private SecurityConfig securityConfig;

    public GlobalConfig() {
        this.debugMode = false;
        this.threadPoolSize = Runtime.getRuntime().availableProcessors();
        this.cacheTimeout = 3600000; // 1 hour
        this.defaultConfidenceThreshold = 0.7;
        this.apiKeys = new HashMap<>();
        this.performanceConfig = new PerformanceConfig();
        this.securityConfig = new SecurityConfig();
    }

    public void load(ConfigurationSection config) {
        if (config == null) return;
        
        this.debugMode = config.getBoolean("debug-mode", debugMode);
        this.threadPoolSize = config.getInt("thread-pool-size", threadPoolSize);
        this.cacheTimeout = config.getLong("cache-timeout", cacheTimeout);
        this.defaultConfidenceThreshold = config.getDouble("default-confidence-threshold", defaultConfidenceThreshold);
        
        // Load API keys
        ConfigurationSection apiSection = config.getConfigurationSection("api-keys");
        if (apiSection != null) {
            apiSection.getKeys(false).forEach(key -> 
                apiKeys.put(key, apiSection.getString(key)));
        }

        // Load performance config
        ConfigurationSection perfSection = config.getConfigurationSection("performance");
        if (perfSection != null) {
            performanceConfig.load(perfSection);
        }

        // Load security config
        ConfigurationSection secSection = config.getConfigurationSection("security");
        if (secSection != null) {
            securityConfig.load(secSection);
        }
    }

    @Getter
    @Setter
    public static class PerformanceConfig {
        private int maxConcurrentRequests;
        private long requestTimeout;
        private int maxRetries;
        private boolean enableCaching;
        private long cacheExpiration;

        public PerformanceConfig() {
            this.maxConcurrentRequests = 10;
            this.requestTimeout = 5000;
            this.maxRetries = 3;
            this.enableCaching = true;
            this.cacheExpiration = 3600000;
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.maxConcurrentRequests = config.getInt("max-concurrent-requests", maxConcurrentRequests);
            this.requestTimeout = config.getLong("request-timeout", requestTimeout);
            this.maxRetries = config.getInt("max-retries", maxRetries);
            this.enableCaching = config.getBoolean("enable-caching", enableCaching);
            this.cacheExpiration = config.getLong("cache-expiration", cacheExpiration);
        }
    }

    @Getter
    @Setter
    public static class SecurityConfig {
        private boolean enableRateLimit;
        private int maxRequestsPerMinute;
        private boolean requireAuthentication;
        private Map<String, String> apiKeys;

        public SecurityConfig() {
            this.enableRateLimit = true;
            this.maxRequestsPerMinute = 60;
            this.requireAuthentication = true;
            this.apiKeys = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enableRateLimit = config.getBoolean("enable-rate-limit", enableRateLimit);
            this.maxRequestsPerMinute = config.getInt("max-requests-per-minute", maxRequestsPerMinute);
            this.requireAuthentication = config.getBoolean("require-authentication", requireAuthentication);
            
            // Load API keys
            ConfigurationSection apiSection = config.getConfigurationSection("api-keys");
            if (apiSection != null) {
                apiSection.getKeys(false).forEach(key ->
                    apiKeys.put(key, apiSection.getString(key)));
            }
        }
    }
}
