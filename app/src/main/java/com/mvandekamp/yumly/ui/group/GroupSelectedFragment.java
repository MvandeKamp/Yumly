package com.mvandekamp.yumly.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingGroup;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.models.RecipeState;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.CookingGroupDao;
import com.mvandekamp.yumly.models.data.DatabaseClient;
import com.mvandekamp.yumly.models.data.RecipeDao;

import java.util.ArrayList;
import java.util.List;

public class GroupSelectedFragment extends Fragment {

    private EditText groupNameInput;
    private EditText groupDescriptionInput;
    private Button saveButton;
    private RecyclerView recipeRecyclerView;

    private CookingGroupDao cookingGroupDao;
    private RecipeDao recipeDao;
    private CookingGroup currentGroup;
    private GroupSelectedRecipesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editor_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        groupNameInput = view.findViewById(R.id.groupNameEditText);
        groupDescriptionInput = view.findViewById(R.id.groupDescriptionEditText);
        saveButton = view.findViewById(R.id.saveGroupButton);
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);

        // Initialize the database and DAO
        AppDatabase db = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        cookingGroupDao = db.cookingGroupDao();
        recipeDao = db.recipeDao();

        // Set up RecyclerView
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Retrieve group ID from arguments
        int groupId = getArguments() != null ? getArguments().getInt("groupId") : -1;

        // Load group details from the database
        if (groupId != -1) {
            new Thread(() -> {
                currentGroup = cookingGroupDao.getGroupById(groupId);
                if (currentGroup != null) {
                    requireActivity().runOnUiThread(() -> {
                        // Populate group details
                        groupNameInput.setText(currentGroup.name);
                        groupDescriptionInput.setText(currentGroup.description);

                        // Set up the adapter with the selected recipes
                        List<RecipeState> selectedRecipes = currentGroup.selectedRecipes;
                        adapter = new GroupSelectedRecipesAdapter(requireContext(), selectedRecipes);
                        recipeRecyclerView.setAdapter(adapter);
                    });
                }
            }).start();
        }

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            if (currentGroup != null) {
                currentGroup.name = groupNameInput.getText().toString();
                currentGroup.description = groupDescriptionInput.getText().toString();

                new Thread(() -> cookingGroupDao.update(currentGroup)).start();
            }
        });

        // Change Recipes button click listener
        Button changeRecipesButton = view.findViewById(R.id.changeSelectedRecipes);
        changeRecipesButton.setOnClickListener(v -> openChangeRecipesDialog());

        // Generate List button click listener
        Button generateListButton = view.findViewById(R.id.generateShoppingList);
        generateListButton.setOnClickListener(v -> generateAndDisplayShoppingList());
    }

    private void generateAndDisplayShoppingList() {
        if (currentGroup != null && currentGroup.selectedRecipes != null) {
            // Extract recipes from RecipeState
            List<Recipe> selectedRecipes = new ArrayList<>();
            for (RecipeState recipeState : currentGroup.selectedRecipes) {
                selectedRecipes.add(recipeState.recipe);
            }

            // Generate the shopping list
            ShoppingListGenerator shoppingListGenerator = new ShoppingListGenerator(selectedRecipes);
            String[] shoppingList = shoppingListGenerator.getShoppingListAsStrings();

            // Display the shopping list in a dialog
            requireActivity().runOnUiThread(() -> {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                builder.setTitle("Shopping List");

                // Convert the shopping list to a single string for display
                StringBuilder shoppingListText = new StringBuilder();
                for (String item : shoppingList) {
                    shoppingListText.append("- ").append(item).append("\n");
                }

                builder.setMessage(shoppingListText.toString());
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });
        } else {
            // Show a message if no recipes are selected
            requireActivity().runOnUiThread(() -> {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                builder.setTitle("No Recipes Selected");
                builder.setMessage("Please select recipes to generate a shopping list.");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });
        }
    }

    private void openChangeRecipesDialog() {
        new Thread(() -> {
            // Fetch all recipes from the database
            List<Recipe> allRecipes = recipeDao.getAllRecipes().getValue();

            requireActivity().runOnUiThread(() -> {
                // Create a dialog
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_recipes, null);
                builder.setView(dialogView);

                RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.dialogRecipeRecyclerView);
                dialogRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                // Set up the adapter
                RecipeSelectedAdapter dialogAdapter = new RecipeSelectedAdapter(requireContext(), allRecipes);
                dialogRecyclerView.setAdapter(dialogAdapter);

                // Add dialog buttons
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    // Get selected recipes
                    List<Recipe> selectedRecipes = dialogAdapter.getSelectedRecipes();

                    // Convert List<Recipe> to List<RecipeState>
                    List<RecipeState> selectedRecipeStates = new ArrayList<>();
                    for (Recipe recipe : selectedRecipes) {
                        RecipeState recipeState = new RecipeState();
                        recipeState.recipe = recipe;
                        recipeState.state = RecipeState.RecipeStates.Created; // Default state
                        selectedRecipeStates.add(recipeState);
                    }

                    // Update the group's selected recipes
                    currentGroup.selectedRecipes = selectedRecipeStates;

                    // Refresh the main RecyclerView
                    adapter.notifyDataSetChanged();
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                // Show the dialog
                builder.create().show();
            });
        }).start();
    }
}