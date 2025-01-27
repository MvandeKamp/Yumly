package com.mvandekamp.yumly.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mvandekamp.yumly.MainActivity;
import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.Ingridient;
import com.mvandekamp.yumly.models.Inventory;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;
import com.mvandekamp.yumly.ui.inventory.InventoryFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageProcessor {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "your_openai_api_key"; // Replace with your OpenAI API key

    /**
     * Sends a Base64-encoded image and a prompt to the OpenAI API for processing.
     *
     * @param context   The application context for showing Toast messages.
     * @param imageBitmap The Bitmap of the image to process.
     * @param prompt    The text prompt to send along with the image.
     */

    public static void sendImageToOpenAI(Context context, Bitmap imageBitmap, String prompt, String formatType) {
        try {
            // Prepare the request payload
            JSONObject requestBody = createRequestPayload(imageBitmap, prompt, formatType);

            // Send the request
            sendRequestToOpenAI(context, requestBody, formatType);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "Error creating request payload.");
        }
    }

    private static JSONObject createRequestPayload(Bitmap imageBitmap, String prompt, String formatType) throws JSONException {
        String base64Image = bitmapToBase64(imageBitmap);

        // Create content array
        JSONArray contentArray = new JSONArray();
        contentArray.put(RequestPayloadFactory.createTextContent(prompt));
        contentArray.put(RequestPayloadFactory.createImageContent(base64Image));

        // Create user message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", contentArray);

        // Create messages array
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(userMessage);

        // Use RequestPayloadFactory to get the response format
        JSONObject responseFormat = RequestPayloadFactory.createResponseFormat(formatType);

        // Build the final request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messagesArray);
        requestBody.put("response_format", responseFormat);
        requestBody.put("max_tokens", 2048);

        return requestBody;
    }

    private static void sendRequestToOpenAI(Context context, JSONObject requestBody, String formatType) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast(context, "Failed to send image to OpenAI API.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(context, response, formatType);
            }
        });
    }

    private static void handleResponse(Context context, Response response, String formatType) throws IOException {
        String contentString = extractContentString(context, response);
        if (contentString == null) {
            // If the content string is null, an error occurred, and we already handled it in the helper method.
            return;
        }

        if ("recipe".equalsIgnoreCase(formatType)) {
            handleRecipe(context, contentString);
        } else if ("ingredients".equalsIgnoreCase(formatType)) {
            handleIngredients(context, contentString);
        } else {
            throw new IllegalArgumentException("Invalid format type: " + formatType);
        }

    }

    private static void handleIngredients(Context context, String contentString) {
        try {
            // Parse the content string into a JSON object
            JSONObject contentJson = new JSONObject(contentString);

            // Extract the "ingredients" array from the JSON object
            JSONArray ingredientsArray = contentJson.getJSONArray("ingredients");

            // Create a list to hold the parsed ingredients
            List<Ingridient> ingredients = new ArrayList<>();

            // Loop through the JSON array and convert each object into an Ingridient
            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ingredientJson = ingredientsArray.getJSONObject(i);
                Ingridient ingredient = MetricCookingUnitConverter
                        .parseIngredient(ingredientJson.getString("name")
                                + " " + ingredientJson.getDouble("amount")
                                + " " + ingredientJson.optString("unit", ""));

                ingredient.estimatedExpirationDate = ingredientJson.optString("estimatedExpirationDate", null);
                ingredient.Price = ingredientJson.optString("price", "");

                ingredients.add(ingredient);
            }

            // Get the database instance
            AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();

            // Perform database operations on a background thread
            new Thread(() -> {
                Inventory inventory;

                // Fetch the inventories synchronously
                List<Inventory> inventories = db.inventoryDao().getAllInventoriesSync();

                // Check if inventories exist
                if (inventories == null || inventories.isEmpty()) {
                    // Create a new default inventory if none exist
                    inventory = new Inventory();
                    inventory.name = "Default Inventory";
                    inventory.ingridients = new ArrayList<>();
                    db.inventoryDao().insert(inventory);

                    // Fetch the newly created inventory
                    inventories = db.inventoryDao().getAllInventoriesSync();
                }

                // Use the first inventory
                inventory = inventories.get(0);

                // Add the new ingredients to the inventory
                if (inventory.ingridients == null) {
                    inventory.ingridients = new ArrayList<>();
                }
                inventory.ingridients.addAll(ingredients);

                // Update the inventory in the database
                db.inventoryDao().update(inventory);

                // Log the added ingredients
                Log.println(Log.INFO, "imageProcessor", "Ingredients added: " + new Gson().toJson(ingredients));

                // Show a success message on the main thread
                new android.os.Handler(context.getMainLooper()).post(() ->
                        Toast.makeText(context, "Ingredients added successfully!", Toast.LENGTH_SHORT).show());
            }).start();

        } catch (JSONException e) {
            Log.println(Log.ERROR, "imageProcessor", "Error parsing ingredients JSON: " + e.getMessage());
            showToast(context, "Error parsing ingredients content.");
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Unexpected error while handling ingredients: " + e.getMessage());
            showToast(context, "Unexpected error occurred.");
        }
    }
    private static void handleRecipe(Context context, String contentString) {
        try {
            // Parse the content string into the Recipe class
            Recipe recipe = parseRecipe(contentString);
            if (recipe == null) {
                showToast(context, "Failed to parse recipe.");
                return;
            }

            AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
            db.recipeDao().insert(recipe);
            showToast(context, "Recipe converted successfully: " + recipe.name);
            Log.println(Log.INFO, "imageProcessor", "Parsed Recipe: " + new Gson().toJson(recipe));
        } catch (JsonSyntaxException e) {
            Log.println(Log.ERROR, "imageProcessor", "Error parsing content string into Recipe: " + e.getMessage());
            showToast(context, "Error parsing recipe content.");
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Unexpected error while saving recipe: " + e.getMessage());
            showToast(context, "Unexpected error occurred.");
        }
    }

    private static String extractContentString(Context context, Response response) {
        if (!response.isSuccessful()) {
            showToast(context, "Failed to process image with OpenAI API.");
            return null;
        }

        String responseData;
        try {
            responseData = response.body().string();
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Error reading response body: " + e.getMessage());
            showToast(context, "Error reading OpenAI API response.");
            return null;
        }

        JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(responseData);
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Error parsing response JSON: " + e.getMessage());
            showToast(context, "Error parsing OpenAI API response.");
            return null;
        }

        JSONArray choices;
        try {
            choices = jsonResponse.getJSONArray("choices");
            if (choices.length() == 0) {
                showToast(context, "No response from OpenAI API.");
                return null;
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Error retrieving 'choices' from response: " + e.getMessage());
            showToast(context, "Invalid response format from OpenAI API.");
            return null;
        }

        JSONObject choice;
        try {
            choice = choices.getJSONObject(0);
            if (!choice.has("message")) {
                Log.println(Log.ERROR, "imageProcessor", "Key 'message' not found in 'choice'.");
                showToast(context, "Invalid response format from OpenAI API.");
                return null;
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Error retrieving first choice: " + e.getMessage());
            showToast(context, "Invalid response format from OpenAI API.");
            return null;
        }

        JSONObject message;
        try {
            message = choice.getJSONObject("message");
            return message.getString("content");
        } catch (Exception e) {
            Log.println(Log.ERROR, "imageProcessor", "Error retrieving 'content' from message: " + e.getMessage());
            showToast(context, "Invalid response format from OpenAI API.");
            return null;
        }
    }

    private static Recipe parseRecipe(String contentString) {
        try {
            // Use Gson to parse the JSON string into a Recipe object
            Gson gson = new Gson();
            return gson.fromJson(contentString, Recipe.class);
        } catch (JsonSyntaxException e) {
            Log.println(Log.ERROR, "imageProcessor","Error parsing JSON into Recipe: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a Bitmap to a Base64-encoded string.
     *
     * @param bitmap The Bitmap to encode.
     * @return The Base64-encoded string.
     */
    private static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Shows a Toast message on the main thread.
     *
     * @param context The application context.
     * @param message The message to display.
     */
    private static void showToast(Context context, String message) {
        new android.os.Handler(context.getMainLooper()).post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}