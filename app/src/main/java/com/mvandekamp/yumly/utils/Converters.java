package com.mvandekamp.yumly.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvandekamp.yumly.models.CookingStep;
import com.mvandekamp.yumly.models.Ingridient;
import com.mvandekamp.yumly.models.RecipeState;
import com.mvandekamp.yumly.models.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    // Type Converter for List<String>
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list); // Convert List<String> to JSON String
    }

    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<String>
    }

    // Type Converter for List<Ingridient>
    @TypeConverter
    public static String fromIngridientList(List<Ingridient> ingridients) {
        if (ingridients == null) {
            return null;
        }
        return gson.toJson(ingridients); // Convert List<Ingridient> to JSON String
    }

    @TypeConverter
    public static List<Ingridient> toIngridientList(String value) {
        if (value == null) {
            return new ArrayList<Ingridient>();
        }
        Type listType = new TypeToken<List<Ingridient>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<Ingridient>
    }

    // Type Converter for List<RecipeState>
    @TypeConverter
    public static String fromRecipeStateList(List<RecipeState> recipeStates) {
        if (recipeStates == null) {
            return null;
        }
        return gson.toJson(recipeStates); // Convert List<RecipeState> to JSON String
    }

    @TypeConverter
    public static List<RecipeState> toRecipeStateList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<RecipeState>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<RecipeState>
    }

    // Type Converter for List<Task>
    @TypeConverter
    public static String fromTaskList(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        return gson.toJson(tasks); // Convert List<Task> to JSON String
    }

    @TypeConverter
    public static List<Task> toTaskList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<Task>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<Task>
    }

    // Type Converter for List<CookingStep>
    @TypeConverter
    public static String fromCookingStepList(List<CookingStep> steps) {
        if (steps == null) {
            return null;
        }
        return gson.toJson(steps); // Convert List<CookingStep> to JSON String
    }

    @TypeConverter
    public static List<CookingStep> toCookingStepList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<CookingStep>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<CookingStep>
    }

    // Type Converter for List<Integer>
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return gson.toJson(list); // Convert List<Integer> to JSON String
    }

    @TypeConverter
    public static List<Integer> toIntegerList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(value, listType); // Convert JSON String back to List<Integer>
    }
}