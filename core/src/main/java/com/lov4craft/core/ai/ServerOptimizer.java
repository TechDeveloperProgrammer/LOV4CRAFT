package com.lov4craft.core.ai;

import com.lov4craft.core.LOV4CraftCore;
import com.lov4craft.core.ai.base.AIService;
import com.lov4craft.core.ai.config.AIConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerOptimizer extends AIService {
    private final Map<String, Double> tpsHistory;
    private final Map<String, Double> memoryHistory;
    private final AtomicBoolean isRunning;
    private Thread optimizationThread;

    public ServerOptimizer(LOV4CraftCore plugin, AIConfig aiConfig, String serviceName) {
        super(plugin, aiConfig, serviceName);
        this.tpsHistory = new ConcurrentHashMap<>();
        this.memoryHistory = new ConcurrentHashMap<>();
        this.isRunning = new AtomicBoolean(false);
    }

    @Override
    public void initialize(Map<String, Object> parameters) {
        if (!enabled) return;

        isRunning.set(true);
        optimizationThread = new Thread(this::optimizationLoop);
        optimizationThread.setName("LOV4CRAFT-Optimizer");
        optimizationThread.start();

        updateState("status", "running");
        plugin.getLogger().info("Server optimizer initialized");
    }

    @Override
    public void shutdown() {
        isRunning.set(false);
        if (optimizationThread != null) {
            optimizationThread.interrupt();
            try {
                optimizationThread.join(5000);
            } catch (InterruptedException e) {
                plugin.getLogger().warning("Failed to stop optimization thread gracefully");
            }
        }
        updateState("status", "stopped");
    }

    private void optimizationLoop() {
        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            try {
                monitorPerformance();
                optimizeServer();
                Thread.sleep(30000); // Run every 30 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                plugin.getLogger().warning("Error in optimization loop: " + e.getMessage());
            }
        }
    }

    private void monitorPerformance() {
        // Monitor TPS
        double currentTPS = Bukkit.getTPS()[0];
        tpsHistory.put(String.valueOf(System.currentTimeMillis()), currentTPS);

        // Monitor Memory Usage
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double memoryUsage = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
        memoryHistory.put(String.valueOf(System.currentTimeMillis()), memoryUsage);

        updateState("tps", currentTPS);
        updateState("memory_usage", memoryUsage);

        // Log performance metrics
        if (memoryUsage > 80 || currentTPS < 18.0) {
            plugin.getLogger().warning("Performance Alert:");
            plugin.getLogger().warning("Current TPS: " + currentTPS);
            plugin.getLogger().warning("Memory Usage: " + String.format("%.2f%%", memoryUsage));
        }
    }

    private void optimizeServer() {
        // Optimize chunk loading
        for (World world : Bukkit.getWorlds()) {
            optimizeWorld(world);
        }

        // Run garbage collection if memory usage is high
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double memoryUsage = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
        if (memoryUsage > 80) {
            System.gc();
            updateState("last_gc", System.currentTimeMillis());
        }
    }

    private void optimizeWorld(World world) {
        // Unload unused chunks
        Arrays.asList(world.getLoadedChunks()).stream()
            .filter(chunk -> !chunk.isLoaded() || !isChunkActive(chunk))
            .forEach(chunk -> {
                chunk.unload(true);
                updateState("unloaded_chunks", 
                    ((Integer) state.getOrDefault("unloaded_chunks", 0)) + 1);
            });

        // Limit entity spawns in heavily populated areas
        int entityCount = world.getLivingEntities().size();
        if (entityCount > 500) {
            for (SpawnCategory category : SpawnCategory.values()) {
                int currentLimit = world.getSpawnLimit(category);
                world.setSpawnLimit(category, Math.max(1, currentLimit / 2));
            }
            updateState("spawn_limits_reduced", true);
            updateState("entity_count", entityCount);
        } else {
            // Reset spawn limits to default
            world.setSpawnLimit(SpawnCategory.MONSTER, 70);
            world.setSpawnLimit(SpawnCategory.ANIMAL, 10);
            world.setSpawnLimit(SpawnCategory.WATER_ANIMAL, 5);
            world.setSpawnLimit(SpawnCategory.AMBIENT, 15);
            updateState("spawn_limits_reduced", false);
            updateState("entity_count", entityCount);
        }
    }

    private boolean isChunkActive(org.bukkit.Chunk chunk) {
        return chunk.getWorld().getPlayers().stream()
            .anyMatch(p -> p.getLocation().distance(
                chunk.getBlock(8, 64, 8).getLocation()) <= 128);
    }

    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("tps", Bukkit.getTPS()[0]);
        metrics.put("memory_usage", getMemoryUsage());
        metrics.put("total_entities", getTotalEntities());
        metrics.put("loaded_chunks", getLoadedChunks());
        metrics.put("active_threads", Thread.activeCount());
        return metrics;
    }

    private double getMemoryUsage() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
    }

    private int getTotalEntities() {
        return Bukkit.getWorlds().stream()
                .mapToInt(world -> world.getEntities().size())
                .sum();
    }

    private int getLoadedChunks() {
        return Bukkit.getWorlds().stream()
                .mapToInt(world -> world.getLoadedChunks().length)
                .sum();
    }
}
