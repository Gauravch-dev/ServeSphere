package com.example.servesphere;

public class IntentResult {
    public final IntentType intent;
    public final double confidence;

    public IntentResult(IntentType intent, double confidence) {
        this.intent = intent;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "IntentResult{intent=" + intent + ", confidence=" + confidence + "}";
    }
}
