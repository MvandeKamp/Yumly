package com.mvandekamp.yumly.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mvandekamp.yumly.models.Recipe;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static void sendImageToOpenAI(Context context, Bitmap imageBitmap, String prompt) {
        try {
            // Prepare the request payload
            JSONObject requestBody = createRequestPayload(imageBitmap, prompt);

            // Send the request
            sendRequestToOpenAI(context, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "Error creating request payload.");
        }
    }

    private static JSONObject createRequestPayload(Bitmap imageBitmap, String prompt) throws JSONException {
        String base64Image = bitmapToBase64(imageBitmap);

        // Create content array
        JSONArray contentArray = new JSONArray();
        contentArray.put(createTextContent(prompt));
        contentArray.put(createImageContent(base64Image));

        // Create user message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", contentArray);

        // Create messages array
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(userMessage);

        // Create response format
        JSONObject responseFormat = createResponseFormat();

        // Build the final request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messagesArray);
        requestBody.put("response_format", responseFormat);
        requestBody.put("max_tokens", 300);

        return requestBody;
    }

    private static JSONObject createTextContent(String prompt) throws JSONException {
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        return textContent;
    }

    private static JSONObject createImageContent(String base64Image) throws JSONException {
        JSONObject imageContent = new JSONObject();
        imageContent.put("type", "image_url");

        JSONObject imageUrlObject = new JSONObject();
        imageUrlObject.put("url", "data:image/jpeg;base64," + base64Image);

        imageContent.put("image_url", imageUrlObject);
        return imageContent;
    }

    private static JSONObject createResponseFormat() throws JSONException {
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
                .put("description", new JSONObject().put("type", "string"))
                .put("ingredients", new JSONObject().put("type", "array").put("items", new JSONObject().put("type", "string")))
                .put("steps", new JSONObject().put("type", "array").put("items", cookingStepSchema))
        );
        recipeSchema.put("required", new JSONArray().put("name").put("description").put("ingredients").put("steps"));
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

    private static void sendRequestToOpenAI(Context context, JSONObject requestBody) {
        OkHttpClient client = new OkHttpClient();
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
                handleResponse(context, response);
            }
        });
    }

    private static void handleResponse(Context context, Response response) throws IOException {
        if (response.isSuccessful()) {
            try {
                String responseData = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseData);
                JSONArray choices = jsonResponse.getJSONArray("choices");

                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);

                    if (choice.has("message")) {
                        JSONObject message = choice.getJSONObject("message");
                        String contentString = message.getString("content");

                        try {
                            // Parse the content string into the Recipe class
                            Recipe recipe = parseRecipe(contentString);
                            AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                            if (recipe != null) {
                                db.recipeDao().insert(recipe);
                                showToast(context, "Recipe converted successfully: " + recipe.name);
                                Log.println(Log.INFO, "imageProcessor","Parsed Recipe: " + new Gson().toJson(recipe));
                            } else {
                                showToast(context, "Failed to parse recipe.");
                            }
                        } catch (JsonSyntaxException e) {
                            Log.println(Log.ERROR, "imageProcessor","Error parsing content string into Recipe: " + e.getMessage());
                        }
                    } else {
                        Log.println(Log.ERROR, "imageProcessor","Key 'message' not found in 'choice'.");
                    }
                } else {
                    showToast(context, "No response from OpenAI API.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(context, "Error parsing OpenAI API response.");
            }
        } else {
            showToast(context, "Failed to process image with OpenAI API.");
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