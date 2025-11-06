package com.example.servesphere;

import android.content.Context;

public class MapsRepository {
    private final Context context;

    public MapsRepository(Context context) {
        this.context = context;
    }

    public String getNearbyServiceLocation(double lat, double lng) {
        // Stubbed for now; replace with actual Places/Maps lookup later
        return "Nearest verified professional within ~2km.";
    }
}
