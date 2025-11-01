package com.example.servesphere;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.Booking;
import java.util.ArrayList;
import java.util.List;

public class ViewBookingsActivity extends AppCompatActivity {

    ListView listViewBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        listViewBookings = findViewById(R.id.listViewBookings);

        AppDatabase db = AppDatabase.getInstance(this);
        List<Booking> bookingList = db.bookingDao().getAllBookings();

        ArrayList<String> displayList = new ArrayList<>();
        for (Booking b : bookingList) {
            displayList.add(b.serviceType + " - " + b.date + " @ " + b.location);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, displayList);
        listViewBookings.setAdapter(adapter);
    }
}
