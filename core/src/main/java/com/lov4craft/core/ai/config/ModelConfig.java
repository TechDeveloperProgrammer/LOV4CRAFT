package com.lov4craft.core.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Getter
@Setter
public class ModelConfig {
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

    @Getter
    public enum ModelType {
        NEURAL_NETWORK("neural-network"),
        RANDOM_FOREST("random-forest"),
        GRADIENT_BOOST("gradient-boost"),
        DEEP_LEARNING("deep-learning"),
        TRANSFORMER("transformer"),
        CUSTOM("custom");

        private final String identifier;

        ModelType(String identifier) {
            this.identifier = identifier;
        }
    }

    @Getter
    @Setter
    public static class LayerConfig {
        private int size;
        private String activationFunction;
        private double dropoutRate;
        private Map<String, Object> parameters;

        public LayerConfig() {
            this.size = 64;
            this.activationFunction = "relu";
            this.dropoutRate = 0.0;
            this.parameters = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.size = config.getInt("size", size);
            this.activationFunction = config.getString("activation", activationFunction);
            this.dropoutRate = config.getDouble("dropout", dropoutRate);
            
            ConfigurationSection paramsSection = config.getConfigurationSection("parameters");
            if (paramsSection != null) {
                paramsSection.getKeys(false).forEach(key ->
                    parameters.put(key, paramsSection.get(key)));
            }
        }
    }

    @Getter
    @Setter
    public static class TrainingConfig {
        private int epochs;
        private int batchSize;
        private double learningRate;
        private String optimizer;
        private Map<String, Object> optimizerConfig;

        public TrainingConfig() {
            this.epochs = 100;
            this.batchSize = 32;
            this.learningRate = 0.001;
            this.optimizer = "adam";
            this.optimizerConfig = new HashMap<>();
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.epochs = config.getInt("epochs", epochs);
            this.batchSize = config.getInt("batch-size", batchSize);
            this.learningRate = config.getDouble("learning-rate", learningRate);
            this.optimizer = config.getString("optimizer", optimizer);
            
            ConfigurationSection optimizerSection = config.getConfigurationSection("optimizer-config");
            if (optimizerSection != null) {
                optimizerSection.getKeys(false).forEach(key ->
                    optimizerConfig.put(key, optimizerSection.get(key)));
            }
        }
    }

    @Getter
    @Setter
    public static class InferenceConfig {
        private double confidenceThreshold;
        private int maxBatchSize;
        private boolean useCache;
        private long cacheTimeout;

        public InferenceConfig() {
            this.confidenceThreshold = 0.7;
            this.maxBatchSize = 32;
            this.useCache = true;
            this.cacheTimeout = 3600000; // 1 hour
        }

        public void load(ConfigurationSection config) {
            if (config == null) return;
            
            this.confidenceThreshold = config.getDouble("confidence-threshold", confidenceThreshold);
            this.maxBatchSize = config.getInt("max-batch-size", maxBatchSize);
            this.useCache = config.getBoolean("use-cache", useCache);
            this.cacheTimeout = config.getLong("cache-timeout", cacheTimeout);
        }
    }
}
