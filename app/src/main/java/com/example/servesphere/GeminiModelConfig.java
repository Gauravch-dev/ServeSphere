package com.example.servesphere;

/**
 * Central config for Gemini models used in the ServeSphere app.
 * Keeps model names, endpoints, and tuning parameters organized.
 */
public class GeminiModelConfig {

    // ✅ Gemini model name (you can easily switch to another model later)
    public static final String MODEL_NAME = "gemini-2.0-flash";

    // ✅ Full API endpoint path (for GeminiClient or ChatCoordinator)
    public static final String MODEL_PATH = "models/" + MODEL_NAME + ":generateContent";

    // ✅ Optional tuning parameters (if you extend the request schema later)
    public static final float TEMPERATURE = 0.7f;  // creativity (0–1)
    public static final int MAX_OUTPUT_TOKENS = 512;
    public static final float TOP_P = 0.9f;
    public static final float TOP_K = 40;

    /**
     * Returns a user-friendly display name for UI or logs.
     */
    public static String getDisplayName() {
        return "Gemini Flash (" + MODEL_NAME + ")";
    }

    /**
     * Returns the fully qualified path used in API calls.
     */
    public static String getModelPath() {
        return MODEL_PATH;
    }
}
