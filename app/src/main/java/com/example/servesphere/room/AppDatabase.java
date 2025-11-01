package com.example.servesphere.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Booking.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract BookingDao bookingDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "servesphere_db")
                    .allowMainThreadQueries() // For demo; use background threads in production
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
