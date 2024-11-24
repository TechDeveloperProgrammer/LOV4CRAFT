package com.lov4craft.core.ai.base;

import com.lov4craft.core.LOV4CraftCore;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AIConfig {
    private final LOV4CraftCore plugin;
    private final Map<String, ModelConfig> modelConfigs;
    private final Map<String, ServiceConfig> serviceConfigs;
    
    @Getter
    private final GlobalConfig globalConfig;

    public AIConfig(LOV4CraftCore plugin) {
        this.plugin = plugin;
        this.modelConfigs = new ConcurrentHashMap<>();
        this.serviceConfigs = new ConcurrentHashMap<>();
        this.globalConfig = new GlobalConfig();
        loadConfigurations();
    }

    private void loadConfigurations() {
        File configFile = new File(plugin.getDataFolder(), "ai.yml");
        if (!configFile.exists()) {
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        loadGlobalConfig(config.getConfigurationSection("global"));
        loadModelConfigs(config.getConfigurationSection("models"));
        loadServiceConfigs(config.getConfigurationSection("services"));
    }

    public ModelConfig getModelConfig(String modelName) {
        return modelConfigs.getOrDefault(modelName, ModelConfig.DEFAULT);
    }

    public ServiceConfig getServiceConfig(String serviceName) {
        return serviceConfigs.getOrDefault(serviceName, ServiceConfig.DEFAULT);
    }

    @Getter
    public static class GlobalConfig {
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
    }

    @Getter
    public static class ModelConfig {
        public static final ModelConfig DEFAULT = new ModelConfig();
        
        private String modelType;
        private String version;
        private Map<String, Object> parameters;
        private int inputSize;
        private int outputSize;
        private double learningRate;
        private int batchSize;
        private List<Integer> layerSizes;
        private Map<String, Double> weights;

        public ModelConfig() {
            this.modelType = "default";
            this.version = "1.0.0";
            this.parameters = new HashMap<>();
            this.inputSize = 10;
            this.outputSize = 5;
            this.learningRate = 0.001;
            this.batchSize = 32;
            this.layerSizes = Arrays.asList(64, 32, 16);
            this.weights = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.modelType = config.getString("type", modelType);
            this.version = config.getString("version", version);
            this.inputSize = config.getInt("input-size", inputSize);
            this.outputSize = config.getInt("output-size", outputSize);
            this.learningRate = config.getDouble("learning-rate", learningRate);
            this.batchSize = config.getInt("batch-size", batchSize);
            
            // Load layer sizes
            List<Integer> configLayers = config.getIntegerList("layer-sizes");
            if (!configLayers.isEmpty()) {
                this.layerSizes = configLayers;
            }

            // Load parameters
            ConfigurationSection paramsSection = config.getConfigurationSection("parameters");
            if (paramsSection != null) {
                paramsSection.getKeys(false).forEach(key ->
                    parameters.put(key, paramsSection.get(key)));
            }

            // Load weights
            ConfigurationSection weightsSection = config.getConfigurationSection("weights");
            if (weightsSection != null) {
                weightsSection.getKeys(false).forEach(key ->
                    weights.put(key, weightsSection.getDouble(key)));
            }
        }
    }

    @Getter
    public static class ServiceConfig {
        public static final ServiceConfig DEFAULT = new ServiceConfig();
        
        private boolean enabled;
        private String modelName;
        private double confidenceThreshold;
        private long requestTimeout;
        private int maxRetries;
        private Map<String, Object> parameters;
        private RateLimitConfig rateLimitConfig;
        private MetricsConfig metricsConfig;

        public ServiceConfig() {
            this.enabled = true;
            this.modelName = "default";
            this.confidenceThreshold = 0.7;
            this.requestTimeout = 5000;
            this.maxRetries = 3;
            this.parameters = new HashMap<>();
            this.rateLimitConfig = new RateLimitConfig();
            this.metricsConfig = new MetricsConfig();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enabled = config.getBoolean("enabled", enabled);
            this.modelName = config.getString("model-name", modelName);
            this.confidenceThreshold = config.getDouble("confidence-threshold", confidenceThreshold);
            this.requestTimeout = config.getLong("request-timeout", requestTimeout);
            this.maxRetries = config.getInt("max-retries", maxRetries);

            // Load parameters
            ConfigurationSection paramsSection = config.getConfigurationSection("parameters");
            if (paramsSection != null) {
                paramsSection.getKeys(false).forEach(key ->
                    parameters.put(key, paramsSection.get(key)));
            }

            // Load rate limit config
            ConfigurationSection rateSection = config.getConfigurationSection("rate-limit");
            if (rateSection != null) {
                rateLimitConfig.load(rateSection);
            }

            // Load metrics config
            ConfigurationSection metricsSection = config.getConfigurationSection("metrics");
            if (metricsSection != null) {
                metricsConfig.load(metricsSection);
            }
        }
    }

    @Getter
    public static class PerformanceConfig {
        private int maxConcurrentRequests;
        private long requestTimeout;
        private int maxRetries;
        private boolean enableCaching;
        private long cacheExpiration;

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.maxConcurrentRequests = config.getInt("max-concurrent-requests", 10);
            this.requestTimeout = config.getLong("request-timeout", 5000);
            this.maxRetries = config.getInt("max-retries", 3);
            this.enableCaching = config.getBoolean("enable-caching", true);
            this.cacheExpiration = config.getLong("cache-expiration", 3600000);
        }
    }

    @Getter
    public static class SecurityConfig {
        private boolean enableRateLimit;
        private int maxRequestsPerMinute;
        private boolean requireAuthentication;
        private List<String> allowedIPs;
        private Map<String, String> apiKeys;

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enableRateLimit = config.getBoolean("enable-rate-limit", true);
            this.maxRequestsPerMinute = config.getInt("max-requests-per-minute", 60);
            this.requireAuthentication = config.getBoolean("require-authentication", true);
            this.allowedIPs = config.getStringList("allowed-ips");
            
            // Load API keys
            ConfigurationSection apiSection = config.getConfigurationSection("api-keys");
            if (apiSection != null) {
                this.apiKeys = new HashMap<>();
                apiSection.getKeys(false).forEach(key ->
                    apiKeys.put(key, apiSection.getString(key)));
            }
        }
    }

    @Getter
    public static class RateLimitConfig {
        private boolean enabled;
        private int requestsPerSecond;
        private int burstSize;
        private long penaltyTimeout;

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enabled = config.getBoolean("enabled", true);
            this.requestsPerSecond = config.getInt("requests-per-second", 10);
            this.burstSize = config.getInt("burst-size", 20);
            this.penaltyTimeout = config.getLong("penalty-timeout", 60000);
        }
    }

    @Getter
    public static class MetricsConfig {
        private boolean enabled;
        private List<String> trackedMetrics;
        private long reportingInterval;
        private String storageType;

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enabled = config.getBoolean("enabled", true);
            this.trackedMetrics = config.getStringList("tracked-metrics");
            this.reportingInterval = config.getLong("reporting-interval", 60000);
            this.storageType = config.getString("storage-type", "memory");
        }
    }
}
