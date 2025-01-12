package com.mvandekamp.yumly.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;

    public RecipeAdapter() {
    }
    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each recipe item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current recipe
        Recipe recipe = recipes.get(position);

        // Bind recipe data to the views
        holder.recipeNameTextView.setText(recipe.name);
        holder.recipeDescriptionTextView.setText(recipe.description);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // Add a method to add a new recipe to the list
    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
        notifyItemInserted(recipes.size() - 1);
    }

    // Add a method to update the entire recipe list
    public void updateRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeNameTextView;
        public TextView recipeDescriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
        }
    }
}