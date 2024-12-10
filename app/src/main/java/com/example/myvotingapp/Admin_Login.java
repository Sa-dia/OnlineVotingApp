package com.example.myvotingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Login extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Handle Edge-to-Edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onAdminLoginClick(View view) {
        // Get the email and password input
        EditText emailEditText = findViewById(R.id.adminEmail);
        EditText passwordEditText = findViewById(R.id.adminPassword);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Realtime Database query to verify email and password
        mDatabase.child("Admins").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Iterate over the result
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String storedPassword = snapshot.child("password").getValue(String.class);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Login Successful
                                    Toast.makeText(Admin_Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    // Navigate to Admin Dashboard Activity
                                    Intent intent = new Intent(Admin_Login.this, Admin_Dashboard.class);
                                    intent.putExtra("email", email); // Pass email if needed
                                    startActivity(intent);
                                    finish(); // Close the login activity
                                    return;

                                }
                            }
                            // Invalid credentials
                            Toast.makeText(Admin_Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Admin not found
                            Toast.makeText(Admin_Login.this, "Admin not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database errors
                        Toast.makeText(Admin_Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goToAdminRegisterPage(View view) {
        // Intent to navigate to RegisterActivity
        Intent intent = new Intent(Admin_Login.this, Admin_Register.class);
        startActivity(intent);
    }
}
