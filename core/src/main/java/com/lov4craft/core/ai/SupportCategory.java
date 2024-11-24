package com.lov4craft.core.ai;

import com.lov4craft.core.LOV4CraftCore;
import com.lov4craft.core.ai.base.AIService;
import com.lov4craft.core.ai.config.AIConfig;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AISupport extends AIService {
    private final Map<UUID, SupportSession> activeSessions;
    private final Pattern toxicityPattern;
    private final Map<String, ResponseTemplate> responseTemplates;

    public AISupport(LOV4CraftCore plugin, AIConfig aiConfig, String serviceName) {
        super(plugin, aiConfig, serviceName);
        this.activeSessions = new ConcurrentHashMap<>();
        this.toxicityPattern = Pattern.compile("\\b(spam|hack|cheat|grief|kill|noob)\\b", Pattern.CASE_INSENSITIVE);
        this.responseTemplates = new HashMap<>();
        initializeTemplates();
    }

    @Override
    public void initialize(Map<String, Object> parameters) {
        if (!enabled) return;

        // Load custom response templates if provided
        if (parameters.containsKey("templates")) {
            @SuppressWarnings("unchecked")
            Map<String, String> templates = (Map<String, String>) parameters.get("templates");
            templates.forEach((key, value) -> responseTemplates.put(key, new ResponseTemplate(key, value)));
        }

        updateState("status", "running");
        updateState("active_sessions", 0);
        plugin.getLogger().info("AI Support system initialized");
    }

    @Override
    public void shutdown() {
        activeSessions.clear();
        updateState("status", "stopped");
    }

    public CompletableFuture<SupportResponse> handleQuery(Player player, String query) {
        return executeAsync(() -> {
            SupportSession session = getOrCreateSession(player);
            session.addMessage("user", query);

            // Analyze query
            QueryAnalysis analysis = analyzeQuery(query);
            updateState("last_query_type", analysis.category());

            // Generate response
            String response = generateResponse(analysis, session);
            session.addMessage("assistant", response);

            // Update metrics
            updateState("total_queries", ((Integer) state.getOrDefault("total_queries", 0)) + 1);
            
            return new SupportResponse(
                response,
                analysis.category(),
                analysis.confidence(),
                session.getContext()
            );
        });
    }

    private SupportSession getOrCreateSession(Player player) {
        return activeSessions.computeIfAbsent(player.getUniqueId(),
            id -> new SupportSession(player.getName()));
    }

    private QueryAnalysis analyzeQuery(String query) {
        // Detect query category and intent
        String category = detectCategory(query);
        double confidence = calculateConfidence(query);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("toxicity", isToxic(query));
        metadata.put("length", query.length());
        metadata.put("timestamp", System.currentTimeMillis());

        return new QueryAnalysis(category, confidence, metadata);
    }

    private String generateResponse(QueryAnalysis analysis, SupportSession session) {
        ResponseTemplate template = responseTemplates.getOrDefault(
            analysis.category(), 
            responseTemplates.get("default")
        );


        public void addMessage(String role, String content) {
            messageHistory.put(role + "_" + System.currentTimeMillis(), content);
        }

        public Map<String, String> getMessageHistory() {
            return new HashMap<>(messageHistory);
        }

        public long getSessionDuration() {
            return System.currentTimeMillis() - startTime;
        }
    }

    public enum SupportCategory {
        GENERAL_HELP,
        TECHNICAL_ISSUE,
        GAMEPLAY_QUESTION,
        BUG_REPORT,
        FEATURE_REQUEST,
        MODERATION_ISSUE
    }

    public record SupportResponse(
        String message,
        SupportCategory category,
        double confidence,
        Map<String, Object> metadata
    ) {}
}
