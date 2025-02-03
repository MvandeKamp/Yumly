package com.mvandekamp.yumly.ui.inventory;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.Ingridient;
import com.mvandekamp.yumly.models.Inventory;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;
import com.mvandekamp.yumly.utils.ImageProcessor;
import com.mvandekamp.yumly.utils.MetricCookingUnitConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private Inventory inventory;
    private Button processImageButton;
    private Button addIngredientFab;
    private InventoryViewModel inventoryViewModel;

    // Launchers for handling image capture and selection
    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    if (imageBitmap != null) {
                        ImageProcessor.sendImageToOpenAI(
                                getContext(),
                                imageBitmap,
                                "Extract the ingredients information from the provided image to the provided output format. Only Food and Edibles!!!! Extract the ingredients to Metric units if price, date, amount (amount is often in the name, metric or imperial like 1kg or 1lb so use that as the amount) is not Available leave it empty. Available units: ml, l, kg, g, tbsp, tsp and default if none eligible 'unit'",
                                "ingredients"
                        );
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
                        ImageProcessor.sendImageToOpenAI(
                                getContext(),
                                imageBitmap,
                                "Extract the ingredients information from the provided image to the provided output format. Only Food and Edibles!!!! Extract the ingredients to Metric units if price, date, amount (amount is often in the name metric or imperial like 1kg or 1lb) is not Available leave it empty. Available units: ml, l, kg, g, tbsp, tsp and default if none eligible 'unit'",
                                "ingredients"
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    public InventoryFragment() {
        super(R.layout.fragment_inventory);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        AppDatabase database = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        InventoryViewModelFactory factory = new InventoryViewModelFactory(database);
        inventoryViewModel = new ViewModelProvider(this, factory).get(InventoryViewModel.class);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new InventoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        Button clearInventoryButton = view.findViewById(R.id.clearInventoryButton);
        clearInventoryButton.setOnClickListener(v -> clearInventory());

        // Observe the list of inventories
        inventoryViewModel.getInventories().observe(getViewLifecycleOwner(), inventories -> {
            if (inventories != null && !inventories.isEmpty()) {
                // Use the first inventory
                this.inventory = inventories.get(0);
                adapter.updateIngredients(inventory.ingridients);
            } else {
                // If no inventories exist, create a default one
                createDefaultInventory();
            }
        });

        // Set click listener for ingredients
        adapter.setOnIngredientClickListener(this::showEditIngredientDialog);

        // Initialize buttons
        processImageButton = view.findViewById(R.id.processImageButton);
        processImageButton.setOnClickListener(v -> showImageOptions());

        addIngredientFab = view.findViewById(R.id.addIngredientFab);
        addIngredientFab.setOnClickListener(v -> showAddIngredientDialog());

        return view;
    }

    private void createDefaultInventory() {
        Inventory defaultInventory = new Inventory();
        defaultInventory.name = "Default Inventory";
        defaultInventory.ingridients = new ArrayList<>();
        defaultInventory.ingridients.add(new Ingridient("Milk", 2.0, MetricCookingUnitConverter.MetricUnit.LITERS, "2025-01-10", "10€"));
        defaultInventory.ingridients.add(new Ingridient("Eggs", 12.0, MetricCookingUnitConverter.MetricUnit.UNIT, "2025-01-15", "5€"));
        defaultInventory.ingridients.add(new Ingridient("Bread", 1.0, MetricCookingUnitConverter.MetricUnit.UNIT, "2025-01-08", "1€"));

        inventoryViewModel.insertInventory(defaultInventory);
    }

    private void clearInventory() {
        if (inventory != null) {
            inventory.ingridients.clear();
            inventoryViewModel.updateInventory(inventory);
            Toast.makeText(getContext(), "Inventory cleared!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditIngredientDialog(Ingridient ingredient, int position) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.editor_ingredient, null);

        // Initialize dialog views
        EditText ingredientInputEditText = dialogView.findViewById(R.id.ingredientInputEditText);
        EditText ingredientExpirationEditText = dialogView.findViewById(R.id.ingredientExpirationEditText);
        EditText ingredientPriceEditText = dialogView.findViewById(R.id.ingredientPriceEditText);

        // Pre-fill the fields with the ingredient data
        ingredientInputEditText.setText(ingredient.toString());
        ingredientExpirationEditText.setText(ingredient.estimatedExpirationDate);
        ingredientPriceEditText.setText(ingredient.Price);

        // Create and show the dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Edit Ingredient")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update the ingredient with new values
                    Ingridient ingridient = MetricCookingUnitConverter
                            .parseIngredient(ingredientInputEditText.getText().toString());
                    ingredient.name = ingridient.name;
                    ingredient.amount = ingridient.amount;
                    ingredient.unit = ingridient.unit;
                    ingredient.estimatedExpirationDate = ingredientExpirationEditText.getText().toString();
                    ingredient.Price = ingredientPriceEditText.getText().toString();

                    // Update the inventory and database
                    inventory.ingridients.set(position, ingredient);
                    inventoryViewModel.updateInventory(inventory);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showImageOptions() {
        // Show options to take a picture or choose from storage
        String[] options = {"Take Picture", "Choose from Gallery"};
        new AlertDialog.Builder(getContext())
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

    private void showAddIngredientDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.editor_ingredient, null);

        // Initialize dialog views
        EditText ingredientInputEditText = dialogView.findViewById(R.id.ingredientInputEditText);

        // Create and show the dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Add Ingredient")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Get input value
                    String input = ingredientInputEditText.getText().toString();

                    try {
                        // Parse the input using MetricCookingUnitConverter
                        Ingridient newIngredient = MetricCookingUnitConverter.parseIngredient(input);

                        // Add the ingredient to the inventory
                        inventory.ingridients.add(newIngredient);
                        inventoryViewModel.updateInventory(inventory);
                    } catch (IllegalArgumentException e) {
                        // Handle invalid input format
                        Toast.makeText(getContext(), "Invalid input format. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}