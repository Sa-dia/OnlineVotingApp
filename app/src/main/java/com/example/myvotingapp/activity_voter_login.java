package com.example.myvotingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_voter_login extends AppCompatActivity {

    private static final String TAG = "VoterLoginActivity";
    private EditText nidInput, emailInput;
    private Button loginButton, resultButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voter_login);

        // Initialize input fields and buttons
        nidInput = findViewById(R.id.nidInput);
        emailInput = findViewById(R.id.emailInput);  // Ensure you have the email input field in XML
        loginButton = findViewById(R.id.loginButton);
        resultButton = findViewById(R.id.resultButton);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set up the login button click event
        loginButton.setOnClickListener(v -> {
            String nid = nidInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            // Check if both NID and Email fields are filled
            if (nid.isEmpty()) {
                Toast.makeText(activity_voter_login.this, "NID is required", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(activity_voter_login.this, "Email is required", Toast.LENGTH_SHORT).show();
            } else {
                // If both fields are filled, verify with Firebase
                verifyUser(nid, email);
            }
        });

        // Set up the result button click event
        resultButton.setOnClickListener(this::seeVoteResult);
    }

    private void verifyUser(String nid, String email) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean foundVoter = false;

                // Log the full snapshot to ensure data is being retrieved
                Log.d(TAG, "Full Snapshot: " + dataSnapshot.toString());

                // Iterate through all voters
                for (DataSnapshot voterSnapshot : dataSnapshot.getChildren()) {
                    String storedNid = voterSnapshot.child("nid").getValue(String.class);
                    String storedEmail = voterSnapshot.child("email").getValue(String.class);

                    // Log the NID and Email being checked
                    Log.d(TAG, "Checking NID: " + storedNid + ", Email: " + storedEmail);

                    if (storedNid != null && storedNid.equals(nid) && storedEmail != null && storedEmail.equals(email)) {
                        // Voter found with matching NID and email
                        foundVoter = true;
                        Toast.makeText(activity_voter_login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                 //       goToDashboard();
                        break;
                    }
                }

                if (!foundVoter) {
                    // No matching NID and email found
                    Toast.makeText(activity_voter_login.this, "NID not registered or email does not match", Toast.LENGTH_SHORT).show();
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

    public void goToRegisterPage(View view) {
        // Intent to navigate to RegisterActivity
        Intent intent = new Intent(activity_voter_login.this, MainActivity_Register2.class);
        startActivity(intent);
    }

    public void seeVoteResult(View view) {
        // Intent to navigate to Voting_Result Activity
        Intent intent = new Intent(activity_voter_login.this, Voting_Result.class);
        startActivity(intent);
    }

  /**  private void goToDashboard() {
        // Intent to navigate to the Dashboard or another activity after successful login
        Intent intent = new Intent(activity_voter_login.this, DashboardActivity.class); // Change to your actual dashboard activity
        startActivity(intent);
    }**/
}
