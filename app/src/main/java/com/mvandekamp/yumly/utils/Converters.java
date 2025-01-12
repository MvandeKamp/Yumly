package com.mvandekamp.yumly.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvandekamp.yumly.models.CookingStep;
import com.mvandekamp.yumly.models.InventoryItem;
import com.mvandekamp.yumly.models.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(list); // Convert List<String> to JSON String
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<String>
    }

    @TypeConverter
    public static String fromCookingStepList(List<CookingStep> steps) {
        if (steps == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(steps); // Convert List<CookingStep> to JSON String
    }

    @TypeConverter
    public static List<CookingStep> toCookingStepList(String value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CookingStep>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<CookingStep>
    }

    // Type Converter for List<Integer>
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // Use Java's String.join() to create a comma-separated string
        return list.stream()
                .map(String::valueOf) // Convert each Integer to a String
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    @TypeConverter
    public static List<Integer> toIntegerList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        String[] items = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (String item : items) {
            list.add(Integer.parseInt(item));
        }
        return list; // Convert comma-separated String back to List<Integer>
    }

    // Type Converter for List<Task>
    @TypeConverter
    public static String fromTaskList(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(tasks); // Convert List<Task> to JSON String
    }

    @TypeConverter
    public static List<Task> toTaskList(String value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Task>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<Task>
    }

    // Type Converter for List<InventoryItem>
    @TypeConverter
    public static String fromInventoryItemList(List<InventoryItem> items) {
        if (items == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(items); // Convert List<InventoryItem> to JSON String
    }

    @TypeConverter
    public static List<InventoryItem> toInventoryItemList(String value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<InventoryItem>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<InventoryItem>
    }
}