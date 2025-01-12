package com.mvandekamp.yumly.models.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mvandekamp.yumly.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    void insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM Recipe WHERE id = :id")
    Recipe getRecipeById(int id);

    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();
}
