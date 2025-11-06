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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
            Places.initialize(getApplicationContext(), "AIzaSyBm2Z6Ocjf0mw1lvnNVDRrh8DMzhtsrYqw");
        }

        // Disable manual typing for location field
        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> openPlaceAutocomplete());

        btnPickDate.setOnClickListener(v -> openDatePicker());
        btnPickTime.setOnClickListener(v -> openTimePicker());
        btnSaveBooking.setOnClickListener(v -> saveBooking());
    }

    /** 📅 Opens a date picker dialog */
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

    /** ⏰ Opens a time picker dialog */
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

    /** 📍 Opens Google Places Autocomplete for selecting a location */
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

    /** 📦 Save booking locally and schedule alert */
    private void saveBooking() {
        String serviceType = etServiceType.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (serviceType.isEmpty() || location.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullDateTime = selectedDate + " " + selectedTime;

        Booking booking = new Booking(serviceType, fullDateTime, location);
        AppDatabase.getInstance(this).bookingDao().insert(booking);

        // You can also log coordinates if you want to store them later
        if (selectedLat != 0.0 && selectedLng != 0.0) {
            System.out.println("Location coordinates: " + selectedLat + ", " + selectedLng);
        }

        BookingAlertScheduler.scheduleAlert(this, serviceType, selectedDate);

        Toast.makeText(this, "Booking saved for " + fullDateTime, Toast.LENGTH_SHORT).show();
        finish();
    }

    /** 🔁 Handle location picker result */
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
