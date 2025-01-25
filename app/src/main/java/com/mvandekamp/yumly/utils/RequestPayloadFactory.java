package com.mvandekamp.yumly.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestPayloadFactory {

    public static JSONObject createResponseFormat(String formatType) throws JSONException {
        if ("recipe".equalsIgnoreCase(formatType)) {
            return createRecipeResponseFormat();
        } else if ("ingredients".equalsIgnoreCase(formatType)) {
            return createIngredientResponseFormat();
        } else {
            throw new IllegalArgumentException("Invalid format type: " + formatType);
        }
    }

    public static JSONObject createTextContent(String prompt) throws JSONException {
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        return textContent;
    }

    public static JSONObject createImageContent(String base64Image) throws JSONException {
        JSONObject imageContent = new JSONObject();
        imageContent.put("type", "image_url");

        JSONObject imageUrlObject = new JSONObject();
        imageUrlObject.put("url", "data:image/jpeg;base64," + base64Image);

        imageContent.put("image_url", imageUrlObject);
        return imageContent;
    }

    private static JSONObject createRecipeResponseFormat() throws JSONException {
        // Define the cooking step schema
        JSONObject cookingStepSchema = new JSONObject();
        cookingStepSchema.put("type", "object");
        cookingStepSchema.put("properties", new JSONObject()
                .put("description", new JSONObject().put("type", "string"))
                .put("stepNumber", new JSONObject().put("type", "integer"))
        );
        cookingStepSchema.put("required", new JSONArray().put("description").put("stepNumber"));
        cookingStepSchema.put("additionalProperties", false);

        // Define the recipe schema
        JSONObject recipeSchema = new JSONObject();
        recipeSchema.put("type", "object");
        recipeSchema.put("properties", new JSONObject()
                .put("name", new JSONObject().put("type", "string"))
                .put("servings", new JSONObject().put("type", "integer"))
                .put("description", new JSONObject().put("type", "string"))
                .put("ingredients", new JSONObject().put("type", "array").put("items", new JSONObject().put("type", "string")))
                .put("steps", new JSONObject().put("type", "array").put("items", cookingStepSchema))
        );
        recipeSchema.put("required", new JSONArray().put("name").put("servings").put("description").put("ingredients").put("steps"));
        recipeSchema.put("additionalProperties", false);

        // Define the response format
        JSONObject responseFormat = new JSONObject();
        responseFormat.put("type", "json_schema");
        responseFormat.put("json_schema", new JSONObject()
                .put("name", "recipe")
                .put("schema", recipeSchema)
                .put("strict", true)
        );

        return responseFormat;
    }

    private static JSONObject createIngredientResponseFormat() throws JSONException {
        // Define the ingredient schema
        JSONObject ingredientSchema = new JSONObject();
        ingredientSchema.put("type", "object");
        ingredientSchema.put("properties", new JSONObject()
                .put("name", new JSONObject().put("type", "string"))
                .put("amount", new JSONObject().put("type", "number"))
                .put("unit", new JSONObject().put("type", "string"))
                .put("price", new JSONObject().put("type", "string"))
                .put("estimatedExpirationDate", new JSONObject().put("type", "string"))
        );
        ingredientSchema.put("required", new JSONArray()
                .put("name")
                .put("amount")
                .put("unit")
                .put("price")
                .put("estimatedExpirationDate")
        );
        ingredientSchema.put("additionalProperties", false);

        // Define the array of ingredients
        JSONObject ingredientsArraySchema = new JSONObject();
        ingredientsArraySchema.put("type", "array");
        ingredientsArraySchema.put("items", ingredientSchema);

        // Wrap the array in an object
        JSONObject wrappedSchema = new JSONObject();
        wrappedSchema.put("type", "object");
        wrappedSchema.put("properties", new JSONObject()
                .put("ingredients", ingredientsArraySchema)
        );
        wrappedSchema.put("required", new JSONArray().put("ingredients"));
        wrappedSchema.put("additionalProperties", false);

        // Define the response format
        JSONObject responseFormat = new JSONObject();
        responseFormat.put("type", "json_schema");
        responseFormat.put("json_schema", new JSONObject()
                .put("name", "ingredients")
                .put("schema", wrappedSchema)
                .put("strict", true)
        );

        return responseFormat;
    }
}