package com.mvandekamp.yumly.models;

import java.util.List;

public class Task {
    public int number;
    public String type; // "Einkaufen", "Vorbereiten", etc.
    public String assignedTo; // Member name
    public String description; // For "Vorbereiten", "Servieren", "Custom"
    public int cookingStepNumber; // For "Vorbereiten", "Zubereiten"
    public List<String> ingredients; // For "Einkaufen"
}
