package com.example.myvotingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Voting_Result extends AppCompatActivity {

    private DatabaseReference voteCountRef, usersRef;
    private TableLayout tableLayout;
    private TextView totalVotersTextView, totalMaleVotersTextView, totalFemaleVotersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        tableLayout = findViewById(R.id.tableLayout);
        totalVotersTextView = findViewById(R.id.totalVoters);
        totalMaleVotersTextView = findViewById(R.id.totalMaleVoters);
        totalFemaleVotersTextView = findViewById(R.id.totalFemaleVoters);

        voteCountRef = FirebaseDatabase.getInstance().getReference("VoteCount");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        fetchVoteData();
        fetchUserData();
    }

    private void fetchVoteData() {
        voteCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot candidateSnapshot : dataSnapshot.getChildren()) {
                    String candidateName = candidateSnapshot.child("name").getValue(String.class);
                    String symbolName = candidateSnapshot.child("symbolName").getValue(String.class);
                    long votes = candidateSnapshot.child("votes").getValue(Long.class);

                    // Add the candidate's vote details to the table
                    addCandidateToTable(candidateName, symbolName, votes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Voting_Result.this, "Failed to load vote data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maleVoters = 0;
                int femaleVoters = 0;
                int totalVoters = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String gender = userSnapshot.child("gender").getValue(String.class);
                    String votingStatus = userSnapshot.child("votingStatus").getValue(String.class);

                    if ("Yes".equals(votingStatus)) {
                        totalVoters++;
                        if ("Male".equals(gender)) {
                            maleVoters++;
                        } else if ("Female".equals(gender)) {
                            femaleVoters++;
                        }
                    }
                }

                // Update voter statistics
                updateVoterStats(totalVoters, maleVoters, femaleVoters);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Voting_Result.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCandidateToTable(String candidateName, String symbolName, long votes) {
        TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_candidate, null);

        TextView candidateNameTextView = tableRow.findViewById(R.id.candidateName);
        TextView symbolNameTextView = tableRow.findViewById(R.id.symbolName);
        TextView voteCountTextView = tableRow.findViewById(R.id.voteCount);

        candidateNameTextView.setText(candidateName);
        symbolNameTextView.setText(symbolName);
        voteCountTextView.setText(String.valueOf(votes));

        tableLayout.addView(tableRow);
    }

    private void updateVoterStats(int totalVoters, int maleVoters, int femaleVoters) {
        totalVotersTextView.setText("Total Voters: " + totalVoters);
        totalMaleVotersTextView.setText("Total Male Voters: " + maleVoters);
        totalFemaleVotersTextView.setText("Total Female Voters: " + femaleVoters);
    }
}
