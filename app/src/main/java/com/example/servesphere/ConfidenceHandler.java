package com.example.servesphere;

/**
 * Decides what to do based on confidence score
 * from the ML classifier.
 */
public class ConfidenceHandler {

    // Below this value → use Gemini to decide
    private static final double LOW_CONFIDENCE_THRESHOLD = 0.65;

    // Slightly uncertain but not too low
    private static final double MID_CONFIDENCE_THRESHOLD = 0.75;

    /**
     * Evaluates whether the bot should trust its ML prediction
     * or fallback to Gemini for better understanding.
     */
    public static Decision analyze(double confidence) {
        if (confidence < LOW_CONFIDENCE_THRESHOLD) {
            return Decision.FALLBACK_TO_GEMINI;
        } else if (confidence < MID_CONFIDENCE_THRESHOLD) {
            return Decision.CONFIRM_WITH_GEMINI;
        } else {
            return Decision.USE_ML_RESPONSE;
        }
    }

    public enum Decision {
        USE_ML_RESPONSE,
        CONFIRM_WITH_GEMINI,
        FALLBACK_TO_GEMINI
    }
}
