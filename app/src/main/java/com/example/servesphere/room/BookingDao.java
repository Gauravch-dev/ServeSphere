package com.example.servesphere.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BookingDao {

    @Insert
    void insert(Booking booking);

    @Query("SELECT * FROM bookings")
    List<Booking> getAllBookings();
}
