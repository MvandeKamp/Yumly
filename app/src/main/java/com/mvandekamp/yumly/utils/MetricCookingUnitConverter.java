package com.mvandekamp.yumly.utils;

import com.mvandekamp.yumly.models.Ingridient;

import java.util.*;
import java.util.regex.*;

public class MetricCookingUnitConverter {

    // Enum for standardized metric units
    public enum MetricUnit {
        MILLILITERS("ml"),
        LITERS("l"),
        GRAMS("g"),
        KILOGRAMS("kg"),
        TEASPOONS("tsp"),
        TABLESPOONS("tbsp"),
        UNIT("unit"); // Default unit for items like eggs or loafs

        private final String unit;

        MetricUnit(String unit) {
            this.unit = unit;
        }

        public String getUnit() {
            return unit;
        }

        // Method to get the enum from a string
        public static MetricUnit fromString(String unit) {
            for (MetricUnit metricUnit : MetricUnit.values()) {
                if (metricUnit.unit.equalsIgnoreCase(unit)) {
                    return metricUnit;
                }
            }
            throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }

    // Method to parse input and extract ingredient, amount, and unit
    public static Ingridient parseIngredient(String input) {
        // Regex to match patterns like "30ml Milk", "Milk 30 milliliters", or "2 eggs"
        String regex = "(?i)(\\d+(\\.\\d+)?)(\\s*ml|\\s*milliliters|\\s*l|\\s*liters|\\s*g|\\s*grams|\\s*kg|\\s*kilograms|\\s*tsp|\\s*teaspoons|\\s*tbsp|\\s*tablespoons)?\\s*(.+)|(.+)\\s*(\\d+(\\.\\d+)?)(\\s*ml|\\s*milliliters|\\s*l|\\s*liters|\\s*g|\\s*grams|\\s*kg|\\s*kilograms|\\s*tsp|\\s*teaspoons|\\s*tbsp|\\s*tablespoons)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String amountStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(6);
            String unitStr = matcher.group(3) != null ? matcher.group(3).trim() : matcher.group(8) != null ? matcher.group(8).trim() : null;
            String name = matcher.group(4) != null ? matcher.group(4).trim() : matcher.group(5).trim();

            double amount = Double.parseDouble(amountStr);
            MetricUnit unit = unitStr != null ? standardizeUnit(unitStr) : MetricUnit.UNIT; // Default to UNIT if no unit is found

            return new Ingridient(name, amount, unit);
        } else {
            throw new IllegalArgumentException("Invalid input format: " + input);
        }
    }

    // Method to standardize the unit
    private static MetricUnit standardizeUnit(String unit) {
        unit = unit.toLowerCase();
        switch (unit) {
            case "ml":
            case "milliliters":
                return MetricUnit.MILLILITERS;
            case "l":
            case "liters":
                return MetricUnit.LITERS;
            case "g":
            case "grams":
                return MetricUnit.GRAMS;
            case "kg":
            case "kilograms":
                return MetricUnit.KILOGRAMS;
            case "tsp":
            case "teaspoons":
                return MetricUnit.TEASPOONS;
            case "tbsp":
            case "tablespoons":
                return MetricUnit.TABLESPOONS;
            default:
                throw new IllegalArgumentException("Unknown unit: " + unit);
        }
    }

    // Method to compare ingredient names using Euclidean distance
    public static boolean compareIngredients(String name1, String name2) {
        double threshold = 1.5;

        // Tokenize the names into words
        Set<String> allWords = new HashSet<>();
        String[] tokens1 = name1.toLowerCase().split("\\s+");
        String[] tokens2 = name2.toLowerCase().split("\\s+");
        allWords.addAll(Arrays.asList(tokens1));
        allWords.addAll(Arrays.asList(tokens2));

        // Create frequency vectors for both names
        Map<String, Integer> freq1 = new HashMap<>();
        Map<String, Integer> freq2 = new HashMap<>();
        for (String word : allWords) {
            freq1.put(word, 0);
            freq2.put(word, 0);
        }
        for (String word : tokens1) {
            freq1.put(word, freq1.get(word) + 1);
        }
        for (String word : tokens2) {
            freq2.put(word, freq2.get(word) + 1);
        }

        // Calculate Euclidean distance
        double sum = 0.0;
        for (String word : allWords) {
            int count1 = freq1.get(word);
            int count2 = freq2.get(word);
            sum += Math.pow(count1 - count2, 2);
        }
        double distance = Math.sqrt(sum);

        return distance <= threshold;
    }
}