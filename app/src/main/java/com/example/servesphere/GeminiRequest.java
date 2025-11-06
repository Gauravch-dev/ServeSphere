package com.example.servesphere;

import java.util.List;
import java.util.Map;

/**
 * Represents the request body for Gemini 2.0 Flash API.
 * You can extend this later for images, tools, or system instructions.
 */
public class GeminiRequest {

    public List<Content> contents;
    public Map<String, Object> generationConfig;

    public GeminiRequest(List<Content> contents) {
        this.contents = contents;
    }

    // Nested content structure for Gemini
    public static class Content {
        public String role;
        public List<Part> parts;

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
