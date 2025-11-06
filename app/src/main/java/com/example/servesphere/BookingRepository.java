package com.example.servesphere;

import com.example.servesphere.room.Booking;
import com.example.servesphere.room.BookingDao;

import java.util.List;

public class BookingRepository {
    private final BookingDao dao;

    public BookingRepository(BookingDao dao) {
        this.dao = dao;
    }

    public List<Booking> getAll() {
        return dao.getAllBookings();
    }

    public void insert(Booking b) {
        dao.insert(b);
    }
}
