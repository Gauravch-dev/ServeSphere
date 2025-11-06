package com.example.servesphere;

import java.util.List;

/** Request/Response models aligned to Generative Language v1beta. */
public class GeminiApiModels {

    // Request
    public static class TextPart {
        public String text;
        public TextPart(String text) { this.text = text; }
    }

    public static class Content {
        public String role;            // optional, "user" is fine
        public List<TextPart> parts;
        public Content(String role, List<TextPart> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class GenerateContentRequest {
        public List<Content> contents;
        public GenerateContentRequest(List<Content> contents) {
            this.contents = contents;
        }
    }

    // Response (minimal)
    public static class GenerateContentResponse {
        public List<Candidate> candidates;
    }

    public static class Candidate {
        public Content content;
    }
}
