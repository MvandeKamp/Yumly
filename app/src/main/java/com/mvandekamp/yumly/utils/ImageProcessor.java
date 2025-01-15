package com.mvandekamp.yumly.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
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
            // Convert the image to Base64
            String base64Image = bitmapToBase64(imageBitmap);

            // Create the JSON payload
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrlObject = new JSONObject();
            imageUrlObject.put("url", "data:image/jpeg;base64," + base64Image);
            imageContent.put("image_url", imageUrlObject);

            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", prompt);

            JSONArray contentArray = new JSONArray();
            contentArray.put(textContent);
            contentArray.put(imageContent);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", contentArray);

            JSONArray messagesArray = new JSONArray();
            messagesArray.put(userMessage);

            // Define the JSON schema for the response format
            JSONObject cookingStepSchema = new JSONObject();
            cookingStepSchema.put("type", "object");
            cookingStepSchema.put("properties", new JSONObject()
                    .put("description", new JSONObject().put("type", "string"))
                    .put("stepNumber", new JSONObject().put("type", "integer"))
            );
            cookingStepSchema.put("required", new JSONArray().put("description").put("stepNumber"));
            cookingStepSchema.put("additionalProperties", false);

            JSONObject recipeSchema = new JSONObject();
            recipeSchema.put("type", "object");
            recipeSchema.put("properties", new JSONObject()
                    .put("id", new JSONObject().put("type", "integer"))
                    .put("owner", new JSONObject().put("type", "string"))
                    .put("name", new JSONObject().put("type", "string"))
                    .put("imageUri", new JSONObject().put("type", "string"))
                    .put("description", new JSONObject().put("type", "string"))
                    .put("ingredients", new JSONObject().put("type", "array").put("items", new JSONObject().put("type", "string")))
                    .put("steps", new JSONObject().put("type", "array").put("items", cookingStepSchema))
            );
            recipeSchema.put("required", new JSONArray().put("id").put("owner").put("name").put("imageUri").put("description").put("ingredients").put("steps"));
            recipeSchema.put("additionalProperties", false);

            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_schema");
            responseFormat.put("json_schema", new JSONObject()
                    .put("name", "recipe")
                    .put("schema", recipeSchema)
                    .put("strict", true)
            );

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", messagesArray);
            requestBody.put("response_format", responseFormat);
            requestBody.put("max_tokens", 300);

            // Send the request
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
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray choices = jsonResponse.getJSONArray("choices");
                            if (choices.length() > 0) {
                                JSONObject choice = choices.getJSONObject(0);
                                JSONObject recipeJson = choice.getJSONObject("message").getJSONObject("content");

                                // Parse the JSON into a Recipe object
                                String recipeString = recipeJson.toString();
                                showToast(context, "Recipe JSON: " + recipeString);
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
            });
        } catch (Exception e) {
            e.printStackTrace();
            showToast(context, "Error creating request payload.");
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