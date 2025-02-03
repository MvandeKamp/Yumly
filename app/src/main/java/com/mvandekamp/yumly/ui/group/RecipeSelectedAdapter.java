package com.mvandekamp.yumly.ui.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeSelectedAdapter extends RecyclerView.Adapter<RecipeSelectedAdapter.ViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    public RecipeSelectedAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_select, parent, false); // Use your new CardView layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Bind data to the views
        holder.recipeName.setText(recipe.name);
        holder.recipeDesc.setText(recipe.description);
        holder.recipeServings.setText("Servings: " + recipe.servings);

//        Glide.with(context)
//                .load(recipe.imageUri)
//                .placeholder(R.drawable.placeholder_image) // Add a placeholder image
//                .into(holder.recipeImage);

        // Set the checkbox state
        holder.recipeCheckBox.setChecked(recipe.isSelected());

        // Handle item click to toggle selection
        holder.itemView.setOnClickListener(v -> {
            recipe.setSelected(!recipe.isSelected());
            notifyItemChanged(position);
        });

        // Handle checkbox click
        holder.recipeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recipe.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Method to get selected recipes
    public List<Recipe> getSelectedRecipes() {
        List<Recipe> selectedRecipes = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.isSelected()) {
                selectedRecipes.add(recipe);
            }
        }
        return selectedRecipes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, recipeDesc, recipeServings;
        ImageView recipeImage;
        CheckBox recipeCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDesc = itemView.findViewById(R.id.recipeDesc);
            recipeServings = itemView.findViewById(R.id.recipeServings);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeCheckBox = itemView.findViewById(R.id.recipe_CheckBox);
        }
    }
}