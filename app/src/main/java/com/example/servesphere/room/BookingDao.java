package com.example.servesphere.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * BookingDao
 * Defines Room database operations for the Booking entity.
 */
@Dao
public interface BookingDao {

    @Insert
    void insert(Booking booking);

    @Query("SELECT * FROM bookings ORDER BY id DESC")
    List<Booking> getAllBookings();

    @Query("DELETE FROM bookings")
    void deleteAll();
}
