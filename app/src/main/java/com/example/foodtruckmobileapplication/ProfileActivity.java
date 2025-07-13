package com.example.foodtruckmobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName, editAge, editGender, editEmail;
    private Button btnSave;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageButton btnMap, btnReport, btnAbout, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Link UI elements
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editGender = findViewById(R.id.editGender);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);

        // Bottom navigation buttons
        btnMap = findViewById(R.id.btnMap);
        btnReport = findViewById(R.id.btnReport);
        btnAbout = findViewById(R.id.btnAbout);
        btnProfile = findViewById(R.id.btnProfile);

        // Set navigation actions
        btnMap.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MainActivity.class)));
        btnReport.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ReportActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, AboutActivity.class)));
        btnProfile.setOnClickListener(v -> {
            // Already in ProfileActivity — optional: refresh or do nothing
            Toast.makeText(ProfileActivity.this, "You're already on Profile", Toast.LENGTH_SHORT).show();
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            editEmail.setText(user.getEmail());
            editEmail.setEnabled(false);

            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("name"))
                        editName.setText(documentSnapshot.getString("name"));
                    if (documentSnapshot.contains("age"))
                        editAge.setText(documentSnapshot.getString("age"));
                    if (documentSnapshot.contains("gender"))
                        editGender.setText(documentSnapshot.getString("gender"));
                }
            });

            btnSave.setOnClickListener(v -> {
                String name = editName.getText().toString().trim();
                String age = editAge.getText().toString().trim();
                String gender = editGender.getText().toString().trim();

                Map<String, Object> profileData = new HashMap<>();
                profileData.put("name", name);
                profileData.put("age", age);
                profileData.put("gender", gender);

                Log.d("ProfileActivity", "Trying to save profile data...");

                docRef.set(profileData)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(ProfileActivity.this, "Profile saved", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }
    }
}