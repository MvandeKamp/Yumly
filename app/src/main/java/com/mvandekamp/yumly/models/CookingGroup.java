package com.mvandekamp.yumly.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class CookingGroup {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public List<String> members; // List of member names
    public int linkedInventoryId; // Foreign key to Inventory
    public List<Integer> selectedRecipeIds; // Foreign keys to Recipe
    public List<Task> tasks; // List of tasks
}

