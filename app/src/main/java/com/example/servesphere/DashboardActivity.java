package com.example.servesphere;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

public class DashboardActivity extends AppCompatActivity {
    Button btnBookService, btnViewBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnBookService = findViewById(R.id.btnBookService);
        btnViewBookings = findViewById(R.id.btnViewBookings);

        btnBookService.setOnClickListener(v ->
                Toast.makeText(this, "Feature Coming Soon", Toast.LENGTH_SHORT).show());

        btnViewBookings.setOnClickListener(v ->
                Toast.makeText(this, "Feature Coming Soon", Toast.LENGTH_SHORT).show());
    }
}
