package com.mvandekamp.yumly.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Inventory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String owner;
    public String name;
    public List<InventoryItem> items; // List of inventory items
}

