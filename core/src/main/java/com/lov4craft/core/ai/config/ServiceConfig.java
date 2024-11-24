package com.lov4craft.core.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ServiceConfig {
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

    @Getter
    @Setter
    public static class RateLimitConfig {
        private boolean enabled;
        private int requestsPerSecond;
        private int burstSize;
        private long penaltyTimeout;
        private Map<String, Integer> customLimits;

        public RateLimitConfig() {
            this.enabled = true;
            this.requestsPerSecond = 10;
            this.burstSize = 20;
            this.penaltyTimeout = 60000;
            this.customLimits = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enabled = config.getBoolean("enabled", enabled);
            this.requestsPerSecond = config.getInt("requests-per-second", requestsPerSecond);
            this.burstSize = config.getInt("burst-size", burstSize);
            this.penaltyTimeout = config.getLong("penalty-timeout", penaltyTimeout);

            // Load custom limits
            ConfigurationSection limitsSection = config.getConfigurationSection("custom-limits");
            if (limitsSection != null) {
                limitsSection.getKeys(false).forEach(key ->
                    customLimits.put(key, limitsSection.getInt(key)));
            }
        }
    }

    @Getter
    @Setter
    public static class MetricsConfig {
        private boolean enabled;
        private List<String> trackedMetrics;
        private long reportingInterval;
        private String storageType;
        private Map<String, Object> storageConfig;
        private List<AlertConfig> alerts;

        public MetricsConfig() {
            this.enabled = true;
            this.trackedMetrics = new ArrayList<>();
            this.reportingInterval = 60000;
            this.storageType = "memory";
            this.storageConfig = new HashMap<>();
            this.alerts = new ArrayList<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.enabled = config.getBoolean("enabled", enabled);
            this.trackedMetrics = config.getStringList("tracked-metrics");
            this.reportingInterval = config.getLong("reporting-interval", reportingInterval);
            this.storageType = config.getString("storage-type", storageType);

            // Load storage config
            ConfigurationSection storageSection = config.getConfigurationSection("storage-config");
            if (storageSection != null) {
                storageSection.getKeys(false).forEach(key ->
                    storageConfig.put(key, storageSection.get(key)));
            }

            // Load alerts
            ConfigurationSection alertsSection = config.getConfigurationSection("alerts");
            if (alertsSection != null) {
                alertsSection.getKeys(false).forEach(key -> {
                    ConfigurationSection alertSection = alertsSection.getConfigurationSection(key);
                    if (alertSection != null) {
                        AlertConfig alert = new AlertConfig();
                        alert.load(alertSection);
                        alerts.add(alert);
                    }
                });
            }
        }
    }

    @Getter
    @Setter
    public static class AlertConfig {
        private String metric;
        private String condition;
        private double threshold;
        private String severity;
        private List<String> notifications;
        private Map<String, Object> parameters;

        public AlertConfig() {
            this.metric = "";
            this.condition = ">";
            this.threshold = 0.0;
            this.severity = "warning";
            this.notifications = new ArrayList<>();
            this.parameters = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.metric = config.getString("metric", metric);
            this.condition = config.getString("condition", condition);
            this.threshold = config.getDouble("threshold", threshold);
            this.severity = config.getString("severity", severity);
            this.notifications = config.getStringList("notifications");

            // Load parameters
            ConfigurationSection paramsSection = config.getConfigurationSection("parameters");
            if (paramsSection != null) {
                paramsSection.getKeys(false).forEach(key ->
                    parameters.put(key, paramsSection.get(key)));
            }
        }
    }
}
