package com.example.servesphere;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FindServiceActivity extends FragmentActivity implements OnMapReadyCallback {
//places api(second)=AIzaSyAeC_3LSnTXWEJgQ-NAClE-rgkb6cz_D-0
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private PlacesAdapter adapter;
    private ArrayList<Place> placeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_service);

        recyclerView = findViewById(R.id.recyclerViewPlaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getUserLocation();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 15));
                mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));
                fetchNearbyPlaces(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Unable to fetch location. Try again in open area.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

//AIzaSyBm2Z6Ocjf0mw1lvnNVDRrh8DMzhtsrYqw
    private void fetchNearbyPlaces(double lat, double lng) {
        String apiKey = "AIzaSyAeC_3LSnTXWEJgQ-NAClE-rgkb6cz_D-0"; // Replace with your actual key
        int radius = 2000; // 2 km
        String keyword = "plumber OR electrician OR hardware"; // Better keyword search

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + lat + "," + lng +
                "&radius=" + radius +
                "&keyword=" + keyword +
                "&key=" + apiKey;

        new Thread(() -> {
            try {
                URL requestUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // ✅ Move the log here, after sb is filled
                android.util.Log.d("PlacesAPI", "Response: " + sb.toString());

                JSONObject jsonObject = new JSONObject(sb.toString());

                // If API returned error or denied, show it
                if (jsonObject.has("status")) {
                    String status = jsonObject.getString("status");
                    if (!status.equals("OK")) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Places API status: " + status, Toast.LENGTH_LONG).show()
                        );
                        return;
                    }
                }

                JSONArray results = jsonObject.getJSONArray("results");

                runOnUiThread(() -> {
                    placeList.clear();
                    for (int i = 0; i < results.length(); i++) {
                        try {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.getString("name");
                            String address = place.optString("vicinity", "No address found");
                            JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                            double lat1 = loc.getDouble("lat");
                            double lng1 = loc.getDouble("lng");

                            // Add marker on map
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat1, lng1))
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            // Add to RecyclerView list
                            placeList.add(new Place(name, address));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Update RecyclerView
                    adapter = new PlacesAdapter(placeList);
                    recyclerView.setAdapter(adapter);

                    // ✅ Toast for count
                    Toast.makeText(this, "Found " + placeList.size() + " nearby places", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error fetching places: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
