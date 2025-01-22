package com.mvandekamp.yumly.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final List<String> ingredients;

    public IngredientAdapter(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.ingredientEditText.setText(ingredient);

        // Handle ingredient text changes
        holder.ingredientEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                ingredients.set(position, holder.ingredientEditText.getText().toString());
            }
        });

        // Handle remove button click
        holder.removeButton.setOnClickListener(v -> {
            ingredients.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ingredients.size());
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
        notifyItemInserted(ingredients.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText ingredientEditText;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientEditText = itemView.findViewById(R.id.ingredientEditText);
            removeButton = itemView.findViewById(R.id.removeIngredientButton);
        }
    }
}