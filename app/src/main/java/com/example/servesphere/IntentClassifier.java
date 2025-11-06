package com.example.servesphere;

public interface IntentClassifier {
    /**
     * Predict the user intent and a confidence score [0..1].
     */
    IntentResult predict(String userText);
}
