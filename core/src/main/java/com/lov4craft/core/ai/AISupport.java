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
        SupportCategory category = detectCategory(query);
        double confidence = calculateQueryConfidence(query);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("toxicity", isToxic(query));
        metadata.put("length", query.length());
        metadata.put("timestamp", System.currentTimeMillis());

        return new QueryAnalysis(category, confidence, metadata);
    }

    private String generateResponse(QueryAnalysis analysis, SupportSession session) {
        ResponseTemplate template = responseTemplates.getOrDefault(
            analysis.category().name().toLowerCase(), 
            responseTemplates.get("default")
        );

        if (template == null) {
            return "I'm sorry, I don't understand. Could you please rephrase your question?";
        }

        Map<String, String> variables = new HashMap<>();
        variables.put("player", session.playerName);
        variables.put("category", analysis.category().name());
        variables.put("confidence", String.format("%.2f", analysis.confidence()));

        return template.format(variables);
    }

    private SupportCategory detectCategory(String query) {
        query = query.toLowerCase();
        if (query.contains("how") || query.contains("what")) return SupportCategory.GAMEPLAY_QUESTION;
        if (query.contains("help") || query.contains("stuck")) return SupportCategory.GENERAL_HELP;
        if (query.contains("bug") || query.contains("error")) return SupportCategory.TECHNICAL_ISSUE;
        if (query.contains("suggest") || query.contains("idea")) return SupportCategory.FEATURE_REQUEST;
        if (query.contains("report") || query.contains("abuse")) return SupportCategory.MODERATION_ISSUE;
        return SupportCategory.GENERAL_HELP;
    }

    private double calculateQueryConfidence(String query) {
        // Simple confidence calculation based on query length and structure
        double confidence = 0.5; // Base confidence
        
        // Adjust based on query length
        if (query.length() > 10) confidence += 0.1;
        if (query.length() > 20) confidence += 0.1;
        
        // Adjust based on question marks
        if (query.contains("?")) confidence += 0.1;
        
        // Adjust based on specific keywords
        if (query.contains("how") || query.contains("what") || 
            query.contains("why") || query.contains("when")) {
            confidence += 0.2;
        }

        return Math.min(1.0, confidence);
    }

    private boolean isToxic(String message) {
        return toxicityPattern.matcher(message).find();
    }

    private void initializeTemplates() {
        responseTemplates.put("gameplay_question", new ResponseTemplate(
            "gameplay_question",
            "Here's what you need to know, {player}: {explanation}"
        ));
        responseTemplates.put("general_help", new ResponseTemplate(
            "general_help",
            "I'll help you with that, {player}. First, try to {suggestion}"
        ));
        responseTemplates.put("technical_issue", new ResponseTemplate(
            "technical_issue",
            "I understand you're having technical issues. Let's troubleshoot: {steps}"
        ));
        responseTemplates.put("feature_request", new ResponseTemplate(
            "feature_request",
            "Thanks for the suggestion, {player}! We'll consider adding this feature."
        ));
        responseTemplates.put("moderation_issue", new ResponseTemplate(
            "moderation_issue",
            "I've noted your report, {player}. Our moderators will look into it."
        ));
        responseTemplates.put("default", new ResponseTemplate(
            "default",
            "I understand your question. Let me help you with that."
        ));
    }

    private static class SupportSession {
        private final String playerName;
        private final Map<String, String> messageHistory;
        private final Map<String, Object> context;
        private final long startTime;

        public SupportSession(String playerName) {
            this.playerName = playerName;
            this.messageHistory = new LinkedHashMap<>();
            this.context = new HashMap<>();
            this.startTime = System.currentTimeMillis();
        }

        public void addMessage(String role, String content) {
            messageHistory.put(role + "_" + System.currentTimeMillis(), content);
        }

        public Map<String, Object> getContext() {
            context.put("session_duration", System.currentTimeMillis() - startTime);
            context.put("message_count", messageHistory.size());
            return new HashMap<>(context);
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

    private record QueryAnalysis(
        SupportCategory category,
        double confidence,
        Map<String, Object> metadata
    ) {}

    private record ResponseTemplate(
        String type,
        String template
    ) {
        public String format(Map<String, String> variables) {
            String result = template;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return result;
        }
    }

    public record SupportResponse(
        String message,
        SupportCategory category,
        double confidence,
        Map<String, Object> metadata
    ) {}
}
