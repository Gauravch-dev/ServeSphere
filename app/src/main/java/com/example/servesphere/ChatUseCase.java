package com.example.servesphere;

import android.content.Context;

public class ChatUseCase {

    private final ChatCoordinator chatCoordinator;

    public ChatUseCase(Context context,
                       BookingRepository bookingRepository,
                       ServicesRepository servicesRepository,
                       MapsRepository mapsRepository,
                       GeminiApiService geminiService,
                       String apiKey) {
        this.chatCoordinator = new ChatCoordinator(
                context,
                bookingRepository,
                servicesRepository,
                mapsRepository,
                geminiService,
                apiKey
        );
    }

    public void sendMessage(String userId,
                            String userText,
                            double lat,
                            double lng,
                            ChatCoordinator.Callback callback) {
        chatCoordinator.handleMessage(userId, userText, lat, lng, callback);
    }
}
