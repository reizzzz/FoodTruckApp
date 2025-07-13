package com.example.foodtruckmobileapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AboutActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize SharedPreferences (used for session/logout)
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Navigation buttons
        ImageButton btnMap = findViewById(R.id.btnMap);
        ImageButton btnReport = findViewById(R.id.btnReport);
        ImageButton btnAbout = findViewById(R.id.btnAbout);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        // Logout button
        Button btnLogout = findViewById(R.id.btnLogout);

        // Navigation: Map
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigation: Report
        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, ReportActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigation: About (current page)
        btnAbout.setOnClickListener(v -> {
            // Do nothing or show a toast
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AboutActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Logout button action
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(AboutActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Sign out from Firebase
                        FirebaseAuth.getInstance().signOut();

                        // Optional: also clear SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Redirect to LoginActivity and clear stack
                        Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}