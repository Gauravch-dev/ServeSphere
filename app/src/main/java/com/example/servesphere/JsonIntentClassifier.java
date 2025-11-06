package com.example.servesphere;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Lightweight local intent classifier that reads pre-trained JSON weights.
 * Helps ServeSphere work offline and choose between Gemini or local logic.
 */
public class JsonIntentClassifier {

    private static final String TAG = "JsonIntentClassifier";
    private final Map<String, Map<String, Float>> intentWeights = new HashMap<>();

    public static class Result {
        public final String intent;
        public final float confidence;

        public Result(String intent, float confidence) {
            this.intent = intent;
            this.confidence = confidence;
        }
    }

    public JsonIntentClassifier(Context context) {
        try {
            loadWeights(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load weights: " + e.getMessage());
        }
    }

    /**
     * Load intent weight vectors from JSON (stored in assets folder).
     */
    private void loadWeights(Context context) throws Exception {
        InputStream is = context.getAssets().open("serve_sphere_intent_weights_v4.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        reader.close();
        is.close();

        JSONObject json = new JSONObject(sb.toString());
        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String intent = it.next();
            JSONObject weights = json.getJSONObject(intent);
            Map<String, Float> vector = new HashMap<>();

            for (Iterator<String> w = weights.keys(); w.hasNext(); ) {
                String word = w.next();
                vector.put(word, (float) weights.getDouble(word));
            }

            intentWeights.put(intent, vector);
        }

        Log.i(TAG, "✅ Loaded " + intentWeights.size() + " intents.");
    }

    /**
     * Classify input text using cosine similarity with intent weight vectors.
     */
    public Result classify(String text) {
        text = text.toLowerCase(Locale.ROOT).trim();
        String[] words = text.split("\\s+");

        String bestIntent = "fallback";
        float bestScore = 0f;

        for (Map.Entry<String, Map<String, Float>> entry : intentWeights.entrySet()) {
            float score = computeSimilarity(entry.getValue(), words);
            if (score > bestScore) {
                bestScore = score;
                bestIntent = entry.getKey();
            }
        }

        return new Result(bestIntent, bestScore);
    }

    /**
     * Simple similarity: sum of known word weights / total words.
     */
    private float computeSimilarity(Map<String, Float> weights, String[] words) {
        float sum = 0f;
        for (String word : words) {
            if (weights.containsKey(word)) {
                sum += weights.get(word);
            }
        }
        return sum / (words.length + 1);
    }
}
