package com.example.servesphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button buttonAddBooking;
    private Button buttonViewBookings;
    private Button buttonChatbot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BookingAlertScheduler.checkAndNotifyUpcoming(this);

        // 🔗 Bind views
        welcomeText = findViewById(R.id.welcomeText);
        buttonAddBooking = findViewById(R.id.buttonAddBooking);
        buttonViewBookings = findViewById(R.id.buttonViewBookings);
        buttonChatbot = findViewById(R.id.buttonChatbot);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);

        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindServiceActivity.class);
            startActivity(intent);
        });

        // ➕ Add Booking
        buttonAddBooking.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddBookingActivity.class))
        );

        // 📋 View Bookings
        buttonViewBookings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewBookingsActivity.class))
        );

        // 💬 Chatbot Assistant
        buttonChatbot.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ChatActivity.class))
        );
    }
}
//first=AIzaSyBm2Z6Ocjf0mw1lvnNVDRrh8DMzhtsrYqw