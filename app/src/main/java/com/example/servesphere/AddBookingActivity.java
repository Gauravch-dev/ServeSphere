package com.example.servesphere;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.Booking;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddBookingActivity extends AppCompatActivity {

    private EditText etServiceType, etLocation;
    private TextView tvSelectedDate, tvSelectedTime;
    private Button btnPickDate, btnPickTime, btnSaveBooking;

    private String selectedDate = "";
    private String selectedTime = "";
    private double selectedLat = 0.0;
    private double selectedLng = 0.0;

    private final Calendar bookingCalendar = Calendar.getInstance();
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1001;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        etServiceType = findViewById(R.id.etServiceType);
        etLocation = findViewById(R.id.etLocation);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnSaveBooking = findViewById(R.id.btnSaveBooking);

        // Initialize Google Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAeC_3LSnTXWEJgQ-NAClE-rgkb6cz_D-0");
        }

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> openPlaceAutocomplete());
        btnPickDate.setOnClickListener(v -> openDatePicker());
        btnPickTime.setOnClickListener(v -> openTimePicker());
        btnSaveBooking.setOnClickListener(v -> saveBooking());
    }

    private void openDatePicker() {
        int year = bookingCalendar.get(Calendar.YEAR);
        int month = bookingCalendar.get(Calendar.MONTH);
        int day = bookingCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    bookingCalendar.set(y, m, d);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sdf.format(bookingCalendar.getTime());
                    tvSelectedDate.setText("Selected Date: " + selectedDate);
                },
                year, month, day
        );
        dialog.show();
    }

    private void openTimePicker() {
        int hour = bookingCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = bookingCalendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, h, m) -> {
                    bookingCalendar.set(Calendar.HOUR_OF_DAY, h);
                    bookingCalendar.set(Calendar.MINUTE, m);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    selectedTime = timeFormat.format(bookingCalendar.getTime());
                    tvSelectedTime.setText("Selected Time: " + selectedTime);
                },
                hour, minute, true
        );
        dialog.show();
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("IN")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /** Save booking locally and also upload to Firebase */
    private void saveBooking() {
        String serviceType = etServiceType.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (serviceType.isEmpty() || location.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullDateTime = selectedDate + " " + selectedTime;

        // ✅ Save locally in Room
        Booking booking = new Booking(serviceType, fullDateTime, location);
        AppDatabase.getInstance(this).bookingDao().insert(booking);

        // ✅ Also save to Firebase Firestore
        Map<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("serviceType", serviceType);
        bookingMap.put("dateTime", fullDateTime);
        bookingMap.put("location", location);
        bookingMap.put("latitude", selectedLat);
        bookingMap.put("longitude", selectedLng);
        bookingMap.put("timestamp", System.currentTimeMillis());

        firestore.collection("bookings")
                .add(bookingMap)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Booking also saved to Firebase!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        BookingAlertScheduler.scheduleAlert(this, serviceType, selectedDate);

        Toast.makeText(this, "Booking saved for " + fullDateTime, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                etLocation.setText(place.getAddress());

                if (place.getLatLng() != null) {
                    selectedLat = place.getLatLng().latitude;
                    selectedLng = place.getLatLng().longitude;
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR && data != null) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
