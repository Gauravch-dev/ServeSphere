package com.example.servesphere.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Booking entity class
 * Represents a service booking stored locally and synced with Firebase.
 */
@Entity(tableName = "bookings")
public class Booking {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String serviceType;
    private String date;
    private String location;
    private String notes;
    private String imageUrl;

    // Empty constructor (required by Firebase)
    public Booking() {}

    // Constructor for Room
    public Booking(String serviceType, String date, String location) {
        this.serviceType = serviceType;
        this.date = date;
        this.location = location;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
