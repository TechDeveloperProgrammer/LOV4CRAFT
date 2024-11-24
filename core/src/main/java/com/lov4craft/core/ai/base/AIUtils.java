package com.lov4craft.core.ai.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AIUtils {
    
    // Neural Network Utilities
    public static class NeuralUtils {
        public static double[] normalize(double[] input) {
            double min = Arrays.stream(input).min().orElse(0);
            double max = Arrays.stream(input).max().orElse(1);
            double range = max - min;
            
            return Arrays.stream(input)
                .map(x -> (x - min) / range)
                .toArray();
        }

        public static double[][] createMatrix(int rows, int cols) {
            return new double[rows][cols];
        }

        public static double[] matrixMultiply(double[][] matrix, double[] vector) {
            double[] result = new double[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < vector.length; j++) {
                    result[i] += matrix[i][j] * vector[j];
                }
            }
            return result;
        }

        public static double[] applyActivation(double[] input, Function<Double, Double> activation) {
            return Arrays.stream(input)
                .map(activation::apply)
                .toArray();
        }
    }

    // Natural Language Processing Utilities
    public static class NLPUtils {
        public static List<String> tokenize(String text) {
            return Arrays.asList(text.toLowerCase().split("\\s+"));
        }

        public static Map<String, Integer> createBagOfWords(String text) {
            return tokenize(text).stream()
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        }

        public static double calculateCosineSimilarity(Map<String, Integer> vec1, Map<String, Integer> vec2) {
            Set<String> allWords = new HashSet<>(vec1.keySet());
            allWords.addAll(vec2.keySet());
            
            double dotProduct = 0.0;
            double norm1 = 0.0;
            double norm2 = 0.0;
            
            for (String word : allWords) {
                int val1 = vec1.getOrDefault(word, 0);
                int val2 = vec2.getOrDefault(word, 0);
                dotProduct += val1 * val2;
                norm1 += val1 * val1;
                norm2 += val2 * val2;
            }
            
            return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        }
    }

    // Machine Learning Utilities
    public static class MLUtils {
        public static double calculateEntropy(double[] probabilities) {
            return -Arrays.stream(probabilities)
                .filter(p -> p > 0)
                .map(p -> p * Math.log(p))
                .sum();
        }

        public static double calculateGiniImpurity(double[] probabilities) {
            return 1.0 - Arrays.stream(probabilities)
                .map(p -> p * p)
                .sum();
        }

        public static double[] calculateProbabilities(int[] counts) {
            int total = Arrays.stream(counts).sum();
            return Arrays.stream(counts)
                .mapToDouble(count -> (double) count / total)
                .toArray();
        }
    }

    // Feature Extraction Utilities
    public static class FeatureUtils {
        public static double[] extractPlayerFeatures(Player player) {
            return new double[] {
                player.getHealth(),
                player.getFoodLevel(),
                player.getExp(),
                player.getLevel(),
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()
            };
        }

        public static double[] extractLocationFeatures(Location location) {
            return new double[] {
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                location.getWorld().getTime(),
                location.getBlock().getLightLevel()
            };
        }

        public static Vector calculateMovementVector(Location from, Location to) {
            return to.toVector().subtract(from.toVector());
        }
    }

    // Data Processing Utilities
    public static class DataUtils {
        public static double[] movingAverage(double[] data, int windowSize) {
            double[] result = new double[data.length - windowSize + 1];
            for (int i = 0; i < result.length; i++) {
                double sum = 0;
                for (int j = 0; j < windowSize; j++) {
                    sum += data[i + j];
                }
                result[i] = sum / windowSize;
            }
            return result;
        }

        public static Map<String, Double> calculateStatistics(double[] data) {
            DoubleSummaryStatistics stats = Arrays.stream(data).summaryStatistics();
            Map<String, Double> results = new HashMap<>();
            results.put("mean", stats.getAverage());
            results.put("min", stats.getMin());
            results.put("max", stats.getMax());
            results.put("std", calculateStandardDeviation(data, stats.getAverage()));
            return results;
        }

        private static double calculateStandardDeviation(double[] data, double mean) {
            return Math.sqrt(Arrays.stream(data)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0));
        }
    }

    // Validation Utilities
    public static class ValidationUtils {
        public static void validateProbabilities(double[] probabilities) {
            double sum = Arrays.stream(probabilities).sum();
            if (Math.abs(sum - 1.0) > 1e-6) {
                throw new IllegalArgumentException("Probabilities must sum to 1");
            }
        }

        public static void validateFeatureVector(double[] features, int expectedSize) {
            if (features.length != expectedSize) {
                throw new IllegalArgumentException(
                    String.format("Feature vector must have size %d, got %d", 
                        expectedSize, features.length)
                );
            }
        }

        public static void validateRange(double value, double min, double max) {
            if (value < min || value > max) {
                throw new IllegalArgumentException(
                    String.format("Value must be between %f and %f", min, max)
                );
            }
        }
    }

    // Performance Monitoring Utilities
    public static class PerformanceUtils {
        private static final Map<String, List<Long>> executionTimes = new HashMap<>();

        public static void recordExecutionTime(String operation, long startTime) {
            long duration = System.currentTimeMillis() - startTime;
            executionTimes.computeIfAbsent(operation, k -> new ArrayList<>())
                .add(duration);
        }

        public static Map<String, Double> getPerformanceMetrics(String operation) {
            List<Long> times = executionTimes.get(operation);
            if (times == null || times.isEmpty()) {
                return Collections.emptyMap();
            }

            DoubleSummaryStatistics stats = times.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();

            Map<String, Double> metrics = new HashMap<>();
            metrics.put("avg_time", stats.getAverage());
            metrics.put("max_time", stats.getMax());
            metrics.put("min_time", stats.getMin());
            metrics.put("total_executions", (double) stats.getCount());
            return metrics;
        }
    }
}
