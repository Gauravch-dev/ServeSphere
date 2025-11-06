package com.example.servesphere;

import android.content.Context;

public class ServicesRepository {
    private final Context context;

    public ServicesRepository(Context context) {
        this.context = context;
    }

    public String getAvailableServices() {
        return "Cleaning, Plumbing, Electrical, AC Repair, Painting, Gardening";
    }
}
