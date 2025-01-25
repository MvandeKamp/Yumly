package com.mvandekamp.yumly.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecipeState {
    public Recipe recipe;
    public LocalDateTime plannedFor;
    public RecipeStates state;

    // Method to get the string representation of plannedFor in yyyy-MM-dd format
    public String getPlannedForString() {
        if (plannedFor != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return plannedFor.format(formatter);
        }
        return ""; // Return empty if plannedFor is not set
    }
    public enum RecipeStates {
        Created,
        MissingIngredients,
        ReadyForCooking,
        Done
    }
}
