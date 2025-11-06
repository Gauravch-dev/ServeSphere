package com.example.servesphere;

import java.util.List;

/**
 * Represents the response from Gemini API.
 * Gemini usually sends an array of "candidates" with one or more messages.
 */
public class GeminiResponse {

    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

    /**
     * Safely extracts the first reply text, or a fallback if missing.
     */
    public String getPrimaryText() {
        try {
            if (candidates != null && !candidates.isEmpty()) {
                Candidate c = candidates.get(0);
                if (c.content != null && c.content.parts != null && !c.content.parts.isEmpty()) {
                    return c.content.parts.get(0).text;
                }
            }
        } catch (Exception ignored) {}
        return "I'm sorry, I couldn’t process that right now.";
    }
}
