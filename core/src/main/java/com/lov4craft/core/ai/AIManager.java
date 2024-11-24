package com.lov4craft.core.ai;

import com.lov4craft.core.LOV4CraftCore;
import com.lov4craft.core.ai.config.AIConfig;
import com.lov4craft.core.ai.neural.NeuralNetworkService;
import com.lov4craft.core.ai.nlp.NLPService;
import com.lov4craft.core.ai.ml.MachineLearningService;
import com.lov4craft.core.ai.base.AIService;
import lombok.Getter;

import java.util.*;
import java.util.logging.Level;

public class AIManager {
    private final LOV4CraftCore plugin;
    
    @Getter
    private final AIConfig aiConfig;
    
    @Getter
    private final Map<String, AIService> services;

    public AIManager(LOV4CraftCore plugin, AIConfig aiConfig) {
        this.plugin = plugin;
        this.aiConfig = aiConfig;
        this.services = new HashMap<>();
        initializeServices();
    }

    private void initializeServices() {
        try {
            // Initialize core services
            registerService("optimizer", new ServerOptimizer(plugin, aiConfig, "optimizer"));
            registerService("support", new AISupport(plugin, aiConfig, "support"));
            registerService("voice", new VoiceModulator(plugin, aiConfig, "voice"));
            registerService("content", new ContentGenerator(plugin, aiConfig, "content"));

            // Initialize advanced services
            registerService("neural", new NeuralNetworkService(plugin, aiConfig, "neural"));
            registerService("nlp", new NLPService(plugin, aiConfig, "nlp"));
            registerService("ml", new MachineLearningService(plugin, aiConfig, "ml"));

            // Initialize all registered services
            for (Map.Entry<String, AIService> entry : services.entrySet()) {
                String serviceName = entry.getKey();
                AIService service = entry.getValue();
                service.initialize(aiConfig.getServiceConfig(serviceName).getParameters());
            }

            plugin.getLogger().info("AI services initialized successfully");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize AI services", e);
        }
    }

    private void registerService(String name, AIService service) {
        services.put(name, service);
    }

    public void reload() {
        shutdown();
        initializeServices();
    }

    public void shutdown() {
        services.values().forEach(AIService::shutdown);
        services.clear();
    }

    public void logServicesStatus() {
        plugin.getLogger().info("=== AI Services Status ===");
        services.forEach((name, service) -> 
            logServiceStatus(name, service.isEnabled())
        );
    }

    private void logServiceStatus(String serviceName, boolean enabled) {
        plugin.getLogger().info(String.format("%s: %s", 
            serviceName, 
            enabled ? "§aEnabled" : "§cDisabled"
        ));
    }

    @SuppressWarnings("unchecked")
    public <T extends AIService> T getService(String name) {
        return (T) services.get(name);
    }

    public ServerOptimizer getServerOptimizer() {
        return getService("optimizer");
    }

    public AISupport getAiSupport() {
        return getService("support");
    }

    public VoiceModulator getVoiceModulator() {
        return getService("voice");
    }

    public ContentGenerator getContentGenerator() {
        return getService("content");
    }

    public NeuralNetworkService getNeuralNetworkService() {
        return getService("neural");
    }

    public NLPService getNlpService() {
        return getService("nlp");
    }

    public MachineLearningService getMlService() {
        return getService("ml");
    }

    public boolean isServiceEnabled(String serviceName) {
        AIService service = services.get(serviceName);
        return service != null && service.isEnabled();
    }

    public Map<String, Object> getServiceState(String serviceName) {
        AIService service = services.get(serviceName);
        if (service != null) {
            return service.getState();
        }
        return Collections.emptyMap();
    }

    public void updateServiceState(String serviceName, String key, Object value) {
        AIService service = services.get(serviceName);
        if (service != null) {
            service.updateState(key, value);
        }
    }

    public void clearServiceState(String serviceName) {
        AIService service = services.get(serviceName);
        if (service != null) {
            service.clearState();
        }
    }
}
