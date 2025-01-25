package com.mvandekamp.yumly.models.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mvandekamp.yumly.models.CookingGroup;
import com.mvandekamp.yumly.models.Inventory;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.utils.Converters;

@Database(entities = {Recipe.class, CookingGroup.class, Inventory.class}, version = 9)
@TypeConverters({Converters.class}) // Add this line
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract CookingGroupDao cookingGroupDao();
    public abstract InventoryDao inventoryDao();
}

