package com.mvandekamp.yumly.ui.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.RecipeState;

import java.util.ArrayList;
import java.util.List;

public class GroupSelectedRecipesAdapter extends RecyclerView.Adapter<GroupSelectedRecipesAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<RecipeState> recipeStates;

    // Constructor
    public GroupSelectedRecipesAdapter(Context context, List<RecipeState> recipeStates) {
        this.context = context;
        this.recipeStates = recipeStates != null ? recipeStates : new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // Get the current RecipeState
        RecipeState recipeState = recipeStates.get(position);

        // Safeguard against null RecipeState or Recipe
        if (recipeState == null || recipeState.recipe == null) {
            holder.recipeNameTextView.setText("Unknown Recipe");
            holder.recipeDescriptionTextView.setText("");
            return;
        }

        // Bind the data to the views
        holder.recipeNameTextView.setText(recipeState.recipe.name);
        holder.recipeDescriptionTextView.setText(recipeState.recipe.description);
    }

    // Add this method to update the list
    public void updateData(List<RecipeState> newRecipeStates) {
        recipeStates.clear();
        if (newRecipeStates != null) {
            recipeStates.addAll(newRecipeStates);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recipeStates != null ? recipeStates.size() : 0;
    }

    // ViewHolder class
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImageView;
        private final TextView recipeNameTextView;
        private final TextView recipeDescriptionTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
        }
    }
}