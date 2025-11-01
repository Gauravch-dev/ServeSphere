package com.example.servesphere.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookings")
public class Booking {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String serviceType;
    private String date;
    private String location;
    private String imageUrl; // ✅ New field for Firebase image

    public Booking() {
    }

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
