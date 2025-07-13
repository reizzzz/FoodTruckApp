package com.example.foodtruckmobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import java.util.Arrays;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText etFoodTruckName, etDescription;
    private Button btnSubmit;
    private ImageButton btnMap, btnReport, btnAbout, btnProfile;
    private GoogleMap mMap;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBYuVL_H2-oRPIbaBFv5OvKK-2WMr5dT1w");
        }

        // Bind views
        etFoodTruckName = findViewById(R.id.etFoodTruckName);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnMap = findViewById(R.id.btnMap);
        btnReport = findViewById(R.id.btnReport);
        btnAbout = findViewById(R.id.btnAccount);
        btnProfile = findViewById(R.id.btnProfile);

        // Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map not loaded properly.", Toast.LENGTH_SHORT).show();
        }

        // Google Places Autocomplete
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    if (place.getLatLng() != null) {
                        moveMapAndMarker(place.getLatLng(), place.getName());
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(ReportActivity.this, "Search Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Submit Button Logic
        btnSubmit.setOnClickListener(v -> {
            String name = etFoodTruckName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else if (currentMarker == null) {
                Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
            } else {
                LatLng location = currentMarker.getPosition();

                // Firestore upload
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> placeData = new HashMap<>();
                placeData.put("name", name);
                placeData.put("description", desc);
                placeData.put("lat", location.latitude);
                placeData.put("lng", location.longitude);

                db.collection("places")
                        .add(placeData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Place submitted and saved!", Toast.LENGTH_LONG).show();
                            etFoodTruckName.setText("");
                            etDescription.setText("");
                            if (currentMarker != null) currentMarker.remove();
                            currentMarker = null;
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Navigation buttons
        btnMap.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
        });

        btnReport.setOnClickListener(v -> {
            // Already on this page
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLoc = new LatLng(3.1390, 101.6869); // Kuala Lumpur
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 12));

        mMap.setOnMapLongClickListener(latLng -> {
            if (currentMarker != null) currentMarker.remove();
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        });
    }

    private void moveMapAndMarker(LatLng latLng, String title) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            if (currentMarker != null) currentMarker.remove();
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
    }
}
