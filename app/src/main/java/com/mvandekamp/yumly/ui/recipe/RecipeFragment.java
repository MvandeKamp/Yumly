package com.mvandekamp.yumly.ui.recipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingStep;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;
import com.mvandekamp.yumly.utils.ImageProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeFragment extends Fragment {

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private Button addRecipeFab;
    private Button processImageButton;

    // Launchers for handling image capture and selection
    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    if (imageBitmap != null) {
                        ImageProcessor.sendImageToOpenAI(getContext(), imageBitmap, "What is in this image?");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                        ImageProcessor.sendImageToOpenAI(getContext(), imageBitmap, "Extract the recipe information from the provided image to the provided output format. Extract the ingridents to Metric units. If no servings provided make a guess bust just a number!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

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

        recipeAdapter.setOnRecipeClickListener(this::openRecipeEditor);
        new Thread(() -> {
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            if(recipes != null){
                requireActivity().runOnUiThread(() -> {
                    recipeAdapter.updateRecipes(recipes);
                });
            }
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
            EditText recipeServingsEditText = dialogView.findViewById(R.id.recipeServingsEditText);

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
                        int servings = Integer.parseInt(recipeServingsEditText.getText().toString());

                        // Create a new Recipe object
                        Recipe newRecipe = new Recipe();
                        newRecipe.name = name;
                        newRecipe.description = description;
                        newRecipe.ingredients = Arrays.asList(ingredients.split(","));
                        newRecipe.steps = parseSteps(steps);
                        newRecipe.servings = servings;

                        // Add the recipe to the list and update the RecyclerView
                        recipeAdapter.addRecipe(newRecipe);
                        new Thread(() -> {
                            requireActivity().runOnUiThread(() -> {
                                db.recipeDao().insert(newRecipe);
                            });
                        }).start();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Add the new button for image processing
        processImageButton = view.findViewById(R.id.processImageButton);
        processImageButton.setOnClickListener(v -> {
            // Show options to take a picture or choose from storage
            showImageOptions();
        });

        return view;
    }

    private void openRecipeEditor(Recipe recipe, int position) {
        // Create a custom dialog
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

        // Inflate the editor layout
        View editorView = LayoutInflater.from(getContext()).inflate(R.layout.editor_recipe, null);

        // Initialize editor views
        EditText recipeNameInput = editorView.findViewById(R.id.recipeNameInput);
        EditText recipeDescriptionInput = editorView.findViewById(R.id.recipeDescriptionInput);
        EditText recipeServingsInput = editorView.findViewById(R.id.recipeServingsInput);
        RecyclerView ingredientsRecyclerView = editorView.findViewById(R.id.ingredientsRecyclerView);
        RecyclerView stepsRecyclerView = editorView.findViewById(R.id.stepsRecyclerView);

        // Pre-fill the fields with the recipe data
        recipeNameInput.setText(recipe.name);
        recipeDescriptionInput.setText(recipe.description);
        recipeServingsInput.setText(String.valueOf(recipe.servings));

        // Set up ingredients RecyclerView
        IngredientAdapter ingredientAdapter = new IngredientAdapter(recipe.ingredients);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsRecyclerView.setAdapter(ingredientAdapter);

        // Set up steps RecyclerView
        StepAdapter stepAdapter = new StepAdapter(recipe.steps);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsRecyclerView.setAdapter(stepAdapter);

        // Set the custom view for the dialog
        dialog.setContentView(editorView);

        // Set up Save and Cancel buttons
        Button saveButton = editorView.findViewById(R.id.saveButton);
        Button cancelButton = editorView.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            // Save changes to the recipe
            recipe.name = recipeNameInput.getText().toString();
            recipe.description = recipeDescriptionInput.getText().toString();
            recipe.servings = Integer.parseInt(recipeServingsInput.getText().toString());

            new Thread(() -> {
                AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();
                db.recipeDao().update(recipe);
            }).start();

            recipeAdapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void showImageOptions() {
        // Show options to take a picture or choose from storage
        String[] options = {"Take Picture", "Choose from Gallery"};
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Take a picture
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                            captureImageLauncher.launch(takePictureIntent);
                        }
                    } else if (which == 1) {
                        // Choose from gallery
                        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickImageLauncher.launch(pickPhotoIntent);
                    }
                })
                .show();
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