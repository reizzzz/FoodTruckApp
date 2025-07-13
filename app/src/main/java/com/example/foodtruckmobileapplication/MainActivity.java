package com.example.foodtruckmobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageButton btnAbout, btnReport, btnMap, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Map setup
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Bottom navigation setup
        btnMap = findViewById(R.id.btnMap);
        btnAbout = findViewById(R.id.btnAbout);
        btnReport = findViewById(R.id.btnReport);
        btnProfile = findViewById(R.id.btnProfile);

        btnMap.setOnClickListener(v -> {
            // Already on map, do nothing or refresh
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Uncomment when ReportActivity is available
         btnReport.setOnClickListener(v -> {
             Intent intent = new Intent(MainActivity.this, ReportActivity.class);
             startActivity(intent);
         });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Default location to UiTM Arau
        LatLng uitmArau = new LatLng(6.4414, 100.2761);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uitmArau, 15));

        // Fetch saved places from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("places")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String desc = doc.getString("description");
                        Double lat = doc.getDouble("lat");
                        Double lng = doc.getDouble("lng");

                        if (lat != null && lng != null) {
                            LatLng location = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name)
                                    .snippet(desc));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load markers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
