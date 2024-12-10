package com.example.myvotingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class activity_voter_login extends AppCompatActivity {

    private static final String TAG = "VoterLoginActivity";
    private EditText emailInput;
    private Button loginButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_login);

        // Initialize input fields and buttons
        emailInput = findViewById(R.id.emailInput);  // Ensure you have the email input field in XML
        loginButton = findViewById(R.id.loginButton);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set up the login button click event
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            // Check if the email field is filled
            if (email.isEmpty()) {
                Toast.makeText(activity_voter_login.this, "Email is required", Toast.LENGTH_SHORT).show();
            } else {
                // If the email is filled, verify with Firebase
                verifyUser(email);
            }
        });
    }

    private void verifyUser(String email) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean foundVoter = false;

                // Log the full snapshot to ensure data is being retrieved
                Log.d(TAG, "Full Snapshot: " + dataSnapshot.toString());

                // Iterate through all voters
                for (DataSnapshot voterSnapshot : dataSnapshot.getChildren()) {
                    String storedEmail = voterSnapshot.child("email").getValue(String.class);

                    // Log the email being checked
                    Log.d(TAG, "Checking Email: " + storedEmail);

                    if (storedEmail != null && storedEmail.equals(email)) {
                        // Voter found with matching email
                        foundVoter = true;

                        // Store user data in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("VoterLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("name", voterSnapshot.child("name").getValue(String.class));
                        editor.putString("gender", voterSnapshot.child("gender").getValue(String.class));
                        editor.putString("imageUri", voterSnapshot.child("imageUri").getValue(String.class));
                        editor.putLong("loginTime", System.currentTimeMillis());
                        editor.apply();

                        Toast.makeText(activity_voter_login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        goToDashboard();
                        break;
                    }
                }

                if (!foundVoter) {
                    // No matching email found
                    Toast.makeText(activity_voter_login.this, "Email not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                Toast.makeText(activity_voter_login.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToDashboard() {
        // Navigate to the Voter Dashboard activity
        Intent intent = new Intent(activity_voter_login.this, activity_voter_dashboard.class);
        startActivity(intent);
        finish();  // Optional: Close the login activity after successful login
    }
}