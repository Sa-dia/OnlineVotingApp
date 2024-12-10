package com.example.myvotingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Voting_Result extends AppCompatActivity {

    private DatabaseReference voteCountRef, usersRef;
    private TableLayout tableLayout;
    private TextView totalVotersTextView, totalMaleVotersTextView, totalFemaleVotersTextView;
    private BarChart voteBarChart;
    private PieChart voterPieChart;

    private int maleVoters = 0, femaleVoters = 0, totalVoters = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        tableLayout = findViewById(R.id.tableLayout);
        totalVotersTextView = findViewById(R.id.totalVoters);
        totalMaleVotersTextView = findViewById(R.id.totalMaleVoters);
        totalFemaleVotersTextView = findViewById(R.id.totalFemaleVoters);
        voteBarChart = findViewById(R.id.voteBarChart);
        voterPieChart = findViewById(R.id.voterPieChart);

        voteCountRef = FirebaseDatabase.getInstance().getReference("VoteCount");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        fetchVoteData();
        fetchUserData();
    }

    private void fetchVoteData() {
        voteCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();

                // Loop through the VoteCount data
                for (DataSnapshot candidateSnapshot : dataSnapshot.getChildren()) {
                    String candidateName = candidateSnapshot.child("name").getValue(String.class);
                    String symbolName = candidateSnapshot.child("symbolName").getValue(String.class);
                    long votes = candidateSnapshot.child("votes").getValue(Long.class);

                    // Add the candidate's votes to the chart
                    labels.add(symbolName);
                    entries.add(new BarEntry(entries.size(), votes));

                    // Add the candidate data to the table (optional)
                    addCandidateToTable(candidateName, symbolName, votes);
                }

                // Set BarChart Data
                setBarChartData(entries, labels);
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
                updateVoterStats();
                setPieChartData();
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

    private void setBarChartData(ArrayList<BarEntry> entries, ArrayList<String> labels) {
        BarDataSet barDataSet = new BarDataSet(entries, "Vote Counts");
        barDataSet.setColor(Color.GREEN);

        BarData barData = new BarData(labels, barDataSet);
        voteBarChart.setData(barData);
        voteBarChart.invalidate(); // Refresh the chart
    }

    private void updateVoterStats() {
        totalVotersTextView.setText("Total Voters: " + totalVoters);
        totalMaleVotersTextView.setText("Total Male Voters: " + maleVoters);
        totalFemaleVotersTextView.setText("Total Female Voters: " + femaleVoters);
    }

    private void setPieChartData() {
        // Create a list of voter categories (Male, Female)
        ArrayList<Float> values = new ArrayList<>();
        values.add((float) maleVoters);
        values.add((float) femaleVoters);

        // Create a PieDataSet to hold the data for the pie chart
        PieDataSet pieDataSet = new PieDataSet(values, "Voter Statistics");

        // Set the colors for male and female voters
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED); // Male voters
        colors.add(Color.BLUE); // Female voters
        pieDataSet.setColors(colors);

        // Set the percentages in the pie chart
        pieDataSet.setValueTextSize(14f); // Set text size for percentages
        pieDataSet.setValueFormatter(new PercentFormatter()); // Format values as percentages

        // Create a PieData object using the PieDataSet
        PieData pieData = new PieData(pieDataSet);

        // Apply data to the pie chart and refresh the chart
        voterPieChart.setData(pieData);
        voterPieChart.invalidate(); // Refresh the chart

        // Display the percentages directly on the chart
        voterPieChart.setUsePercentValues(true);
        voterPieChart.getDescription().setEnabled(false); // Disable the description
        voterPieChart.setCenterText("Total Voters"); // Add a center label with "Total Voters"
    }
}
