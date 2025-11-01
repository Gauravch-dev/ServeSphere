package com.example.servesphere;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.servesphere.room.AppDatabase;
import com.example.servesphere.room.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AddBookingActivity extends AppCompatActivity {

    EditText editServiceType, editDate, editLocation;
    Button buttonSaveBooking, buttonCaptureImage;
    ImageView imagePreview;
    Bitmap capturedImageBitmap = null;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        // Initialize UI elements
        editServiceType = findViewById(R.id.editServiceType);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        buttonSaveBooking = findViewById(R.id.buttonSaveBooking);
        buttonCaptureImage = findViewById(R.id .buttonCaptureImage);
        imagePreview = findViewById(R.id.imagePreview);

        // 📸 Capture Image button
        buttonCaptureImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE
                );
            } else {
                openCamera();
            }
        });

        // 💾 Save booking
        buttonSaveBooking.setOnClickListener(v -> saveBooking());
    }

    // 📸 Launch Camera Intent
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    // 📸 Handle Captured Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            capturedImageBitmap = (Bitmap) data.getExtras().get("data");
            imagePreview.setImageBitmap(capturedImageBitmap);
        }
    }

    // 💾 Save Booking Data
    private void saveBooking() {
        String service = editServiceType.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (service.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1️⃣ Save booking to Room Database
        AppDatabase db = AppDatabase.getInstance(this);
        Booking booking = new Booking(service, date, location);
        db.bookingDao().insert(booking);

        // 2️⃣ Save booking to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bookingsRef = database.getReference("bookings");

        // If image is captured, upload it to Firebase Storage
        if (capturedImageBitmap != null) {
            uploadImageAndSaveBooking(bookingsRef, booking);
        } else {
            bookingsRef.push().setValue(booking);
            Toast.makeText(this, "Booking Saved Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // ☁️ Upload image to Firebase Storage
    private void uploadImageAndSaveBooking(DatabaseReference bookingsRef, Booking booking) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("booking_images");

        // Convert bitmap to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        capturedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageData = baos.toByteArray();

        // Create unique file name
        StorageReference imageRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Save download URL to booking object
                    booking.setImageUrl(uri.toString());
                    bookingsRef.push().setValue(booking);
                    Toast.makeText(this, "Booking + Image Uploaded!", Toast.LENGTH_SHORT).show();
                    finish();
                })
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
