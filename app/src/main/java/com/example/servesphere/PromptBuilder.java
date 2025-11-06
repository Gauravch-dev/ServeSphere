package com.example.servesphere;

import android.content.Context;

import com.example.servesphere.room.Booking;
import com.example.servesphere.room.BookingDao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class PromptBuilder {

    private final Context context;
    private final BookingRepository bookingRepo;
    private final ServicesRepository servicesRepo;
    private final MapsRepository mapsRepo;

    public PromptBuilder(Context context,
                         BookingRepository bookingRepo,
                         ServicesRepository servicesRepo,
                         MapsRepository mapsRepo) {
        this.context = context;
        this.bookingRepo = bookingRepo;
        this.servicesRepo = servicesRepo;
        this.mapsRepo = mapsRepo;
    }

    /** Build a compact JSON string we will place inside Gemini's text part. */
    public String buildPromptJson(String userId, String userMessage, double lat, double lng) {
        try {
            JSONObject root = new JSONObject();
            root.put("assistant_role", "ServeSphere Assistant");
            root.put("user_id", userId);
            root.put("message", userMessage);
            root.put("user_location", lat + "," + lng);

            // available services
            root.put("available_services", servicesRepo.getAvailableServices());

            // local bookings (Room)
            List<Booking> bookings = bookingRepo.getAll();
            JSONArray arr = new JSONArray();
            for (Booking b : bookings) {
                JSONObject jb = new JSONObject();
                jb.put("service_type", b.getServiceType());
                jb.put("date", b.getDate());
                jb.put("location", b.getLocation());
                arr.put(jb);
            }
            root.put("local_bookings", arr);

            // nearby hint (stub)
            root.put("nearby_hint", mapsRepo.getNearbyServiceLocation(lat, lng));

            // instruction to keep the response short
            root.put("style", "Reply briefly and helpfully.");

            return root.toString();
        } catch (Exception e) {
            return "{\"error\":\"prompt_build_failed\"}";
        }
    }
}
