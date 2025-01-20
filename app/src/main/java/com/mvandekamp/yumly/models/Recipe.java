package com.mvandekamp.yumly.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String owner;
    public String name;
    public String imageUri; // Path to the image
    public String description;
    public int servings;
    public List<String> ingredients; // List of ingredients
    public List<CookingStep> steps; // List of cooking steps
}

