package com.example.servesphere;

import android.content.Context;

public class ChatCoordinator {

    public interface Callback {
        void onResult(String reply);
        void onError(Throwable t);
    }

    private static final String MODEL = "gemini-2.0-flash"; // use the Flash model

    private final PromptBuilder promptBuilder;
    private final GeminiApiService geminiService;
    private final String apiKey;

    public ChatCoordinator(Context context,
                           BookingRepository bookingRepo,
                           ServicesRepository servicesRepo,
                           MapsRepository mapsRepo,
                           GeminiApiService geminiService,
                           String apiKey) {
        this.promptBuilder = new PromptBuilder(context, bookingRepo, servicesRepo, mapsRepo);
        this.geminiService = geminiService;
        this.apiKey = apiKey;
    }

    public void handleMessage(String userId,
                              String message,
                              double lat,
                              double lng,
                              Callback callback) {
        try {
            // Build a compact JSON bundle we will send in the 'text' part to Gemini
            String jsonPrompt = promptBuilder.buildPromptJson(userId, message, lat, lng);

            GeminiHelper.callGemini(
                    geminiService,
                    apiKey,
                    MODEL,
                    jsonPrompt,
                    new GeminiHelper.GeminiCallback() {
                        @Override
                        public void onSuccess(String reply) {
                            if (callback != null) callback.onResult(reply);
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (callback != null) callback.onError(t);
                        }
                    }
            );
        } catch (Exception e) {
            if (callback != null) callback.onError(e);
        }
    }
}
