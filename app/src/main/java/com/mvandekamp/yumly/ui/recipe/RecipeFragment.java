package com.mvandekamp.yumly.ui.recipe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.CookingStep;
import com.mvandekamp.yumly.models.data.DatabaseClient;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeFragment extends Fragment {

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private FloatingActionButton addRecipeFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        // Initialize RecyclerView
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter and set it to RecyclerView
        recipeAdapter = new RecipeAdapter();
        recipeRecyclerView.setAdapter(recipeAdapter);

        new Thread(() -> {
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            requireActivity().runOnUiThread(() -> {
                recipeAdapter.updateRecipes(recipes);
            });
        }).start();

        // Initialize FloatingActionButton
        addRecipeFab = view.findViewById(R.id.addRecipeFab);
        addRecipeFab.setOnClickListener(v -> {
            // Handle adding a new recipe
            // Inflate the dialog layout
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_recipe, null);

            // Initialize dialog views
            EditText recipeNameEditText = dialogView.findViewById(R.id.recipeNameEditText);
            EditText recipeDescriptionEditText = dialogView.findViewById(R.id.recipeDescriptionEditText);
            EditText recipeIngredientsEditText = dialogView.findViewById(R.id.recipeIngredientsEditText);
            EditText recipeStepsEditText = dialogView.findViewById(R.id.recipeStepsEditText);

            // Create and show the dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Add Recipe")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        // Get input values
                        String name = recipeNameEditText.getText().toString();
                        String description = recipeDescriptionEditText.getText().toString();
                        String ingredients = recipeIngredientsEditText.getText().toString();
                        String steps = recipeStepsEditText.getText().toString();

                        // Create a new Recipe object
                        Recipe newRecipe = new Recipe();
                        newRecipe.name = name;
                        newRecipe.description = description;
                        newRecipe.ingredients = Arrays.asList(ingredients.split(","));
                        newRecipe.steps = parseSteps(steps);

                        // Add the recipe to the list and update the RecyclerView
                        recipeAdapter.addRecipe(newRecipe);
                        new Thread(() -> {
                            db.recipeDao().insert(newRecipe);
                        }).start();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    // Helper method to parse steps
    private List<CookingStep> parseSteps(String steps) {
        List<CookingStep> stepList = new ArrayList<>();
        String[] stepArray = steps.split(",");
        for (int i = 0; i < stepArray.length; i++) {
            CookingStep step = new CookingStep();
            step.number = i + 1;
            step.title = "Step " + (i + 1);
            step.description = stepArray[i].trim();
            stepList.add(step);
        }
        return stepList;
    }
}