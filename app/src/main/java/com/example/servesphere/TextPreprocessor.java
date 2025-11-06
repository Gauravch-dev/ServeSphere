package com.example.servesphere;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles user input cleaning and basic typo correction before
 * sending to the ML intent classifier or Gemini.
 */
public class TextPreprocessor {

    private static final Map<String, String> typoMap = new HashMap<>();

    static {
        // Common user typo corrections
        typoMap.put("helo", "hello");
        typoMap.put("plmber", "plumber");
        typoMap.put("eletrician", "electrician");
        typoMap.put("gud", "good");
        typoMap.put("mornng", "morning");
        typoMap.put("servce", "service");
        typoMap.put("bok", "book");
        typoMap.put("bokng", "booking");
    }

    /**
     * Cleans up and normalizes user input.
     */
    public static String normalize(String input) {
        if (input == null || input.isEmpty()) return "";

        // Lowercase
        String clean = input.toLowerCase().trim();

        // Remove accents or weird unicode characters
        clean = Normalizer.normalize(clean, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Replace multiple spaces
        clean = clean.replaceAll("\\s+", " ");

        // Fix simple typos
        for (Map.Entry<String, String> entry : typoMap.entrySet()) {
            clean = clean.replace(entry.getKey(), entry.getValue());
        }

        // Remove trailing punctuation
        clean = clean.replaceAll("[!?.,]+$", "");

        return clean;
    }
}
