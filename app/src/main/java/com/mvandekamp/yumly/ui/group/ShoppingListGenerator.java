package com.mvandekamp.yumly.ui.group;

import com.mvandekamp.yumly.models.Ingridient;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.utils.MetricCookingUnitConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListGenerator {

    private final List<Recipe> recipes;
    private final List<Ingridient> shoppingList;

    public ShoppingListGenerator(List<Recipe> recipes) {
        this.recipes = recipes;
        this.shoppingList = new ArrayList<>();
        generateShoppingList();
    }

    // Method to generate the shopping list
    private void generateShoppingList() {
        Map<String, Ingridient> combinedIngredients = new HashMap<>();

        for (Recipe recipe : recipes) {
            if (recipe.isSelected()) {
                for (String ingredientString : recipe.ingredients) {
                    Ingridient ingredient = MetricCookingUnitConverter.parseIngredient(ingredientString);

                    // Check if a similar ingredient already exists in the map
                    boolean found = false;
                    for (Map.Entry<String, Ingridient> entry : combinedIngredients.entrySet()) {
                        Ingridient existingIngredient = entry.getValue();
                        if (MetricCookingUnitConverter.compareIngredients(existingIngredient.getName(), ingredient.getName())
                                && existingIngredient.getUnit() == ingredient.getUnit()) {
                            // Combine amounts if the ingredient is similar and units match
                            existingIngredient.amount += ingredient.getAmount();
                            found = true;
                            break;
                        }
                    }

                    // If no similar ingredient is found, add it to the map
                    if (!found) {
                        combinedIngredients.put(ingredient.getName(), ingredient);
                    }
                }
            }
        }

        // Add all combined ingredients to the shopping list
        shoppingList.addAll(combinedIngredients.values());
    }

    // Method to return the shopping list as an array of strings
    public String[] getShoppingListAsStrings() {
        List<String> shoppingListStrings = new ArrayList<>();
        for (Ingridient ingredient : shoppingList) {
            shoppingListStrings.add(ingredient.toString());
        }
        return shoppingListStrings.toArray(new String[0]);
    }
}