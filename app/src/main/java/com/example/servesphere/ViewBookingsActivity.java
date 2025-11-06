package com.example.servesphere;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewBookingsActivity
 * Displays user bookings from both Room and Firebase Realtime Database.
 */
public class ViewBookingsActivity extends AppCompatActivity {

    private ListView listViewBookings;
    private Button buttonRefresh;
    private ArrayAdapter<String> adapter;
    private List<String> bookingList = new ArrayList<>();

    private DatabaseReference firebaseRef;
    private AppDatabase localDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        listViewBookings = findViewById(R.id.listViewBookings);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        localDb = AppDatabase.getInstance(this);
        firebaseRef = FirebaseDatabase.getInstance().getReference("bookings");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookingList);
        listViewBookings.setAdapter(adapter);

        // Load local + cloud data
        loadLocalBookings();
        loadFirebaseBookings();

        // Manual refresh
        buttonRefresh.setOnClickListener(v -> {
            bookingList.clear();
            loadLocalBookings();
            loadFirebaseBookings();
        });
    }

    /** 📦 Load bookings from local Room database */
    private void loadLocalBookings() {
        new Thread(() -> {
            List<Booking> localBookings = localDb.bookingDao().getAllBookings();
            runOnUiThread(() -> {
                for (Booking booking : localBookings) {
                    bookingList.add(formatBookingText(booking));
                }
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** ☁️ Load bookings from Firebase Realtime Database */
    private void loadFirebaseBookings() {
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    Booking booking = bookingSnap.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(formatBookingText(booking));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewBookingsActivity.this,
                        "Failed to load Firebase data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🧾 Format booking for display */
    private String formatBookingText(Booking booking) {
        StringBuilder sb = new StringBuilder();
        sb.append("📍 ").append(booking.getServiceType())
                .append("\n🗓 ").append(booking.getDate())
                .append("\n📌 ").append(booking.getLocation());
        if (booking.getNotes() != null && !booking.getNotes().isEmpty()) {
            sb.append("\n📝 ").append(booking.getNotes());
        }
        if (booking.getImageUrl() != null && !booking.getImageUrl().isEmpty()) {
            sb.append("\n📷 Image attached");
        }
        return sb.toString();
    }
}
