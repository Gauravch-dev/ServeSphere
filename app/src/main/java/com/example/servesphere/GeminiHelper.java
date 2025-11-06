package com.example.servesphere;

import android.util.Log;

import com.example.servesphere.GeminiApiModels.Content;
import com.example.servesphere.GeminiApiModels.GenerateContentRequest;
import com.example.servesphere.GeminiApiModels.GenerateContentResponse;
import com.example.servesphere.GeminiApiModels.TextPart;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Wraps the call and extracts the primary text reply. */
public class GeminiHelper {

    private static final String TAG = "GeminiHelper";

    public interface GeminiCallback {
        void onSuccess(String reply);
        void onError(Throwable t);
    }

    public static void callGemini(GeminiApiService service,
                                  String apiKey,
                                  String model,
                                  String promptTextJson,
                                  GeminiCallback callback) {
        try {
            // Put our JSON bundle into a single text part
            TextPart part = new TextPart(promptTextJson);
            Content content = new Content("user", Collections.singletonList(part));
            GenerateContentRequest request = new GenerateContentRequest(Collections.singletonList(content));

            Call<GenerateContentResponse> call = service.generateContent(model, apiKey, request);
            call.enqueue(new Callback<GenerateContentResponse>() {
                @Override
                public void onResponse(Call<GenerateContentResponse> call, Response<GenerateContentResponse> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        callback.onError(new Exception("Gemini API error: " + response.code()));
                        return;
                    }
                    GenerateContentResponse body = response.body();
                    if (body.candidates == null || body.candidates.isEmpty()
                            || body.candidates.get(0).content == null
                            || body.candidates.get(0).content.parts == null
                            || body.candidates.get(0).content.parts.isEmpty()
                            || body.candidates.get(0).content.parts.get(0).text == null) {
                        callback.onError(new Exception("Empty Gemini response"));
                        return;
                    }
                    String reply = body.candidates.get(0).content.parts.get(0).text;
                    callback.onSuccess(reply);
                }

                @Override
                public void onFailure(Call<GenerateContentResponse> call, Throwable t) {
                    Log.e(TAG, "Gemini call failed", t);
                    callback.onError(t);
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }
    }
}
