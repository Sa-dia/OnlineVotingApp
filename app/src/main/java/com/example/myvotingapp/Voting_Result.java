package com.example.myvotingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Voting_Result extends AppCompatActivity {

    private DatabaseReference voteCountRef, usersRef;
    private BarChart barChart;

    private PieChart pieChartTotal, pieChartMale, pieChartFemale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        // Initialize charts
        barChart = findViewById(R.id.barChart);
        pieChartTotal = findViewById(R.id.pieChartTotal);  // Initialize pie chart for total voters
        pieChartMale = findViewById(R.id.pieChartMale);
        pieChartFemale = findViewById(R.id.pieChartFemale);

        // Firebase references
        voteCountRef = FirebaseDatabase.getInstance().getReference("VoteCount");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Fetch data for charts
        fetchVoteData();
        fetchUserData();
    }

    private void fetchVoteData() {
        voteCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                ArrayList<String> candidateSymbols = new ArrayList<>();
                int index = 0;

                for (DataSnapshot candidateSnapshot : dataSnapshot.getChildren()) {
                    String candidateSymbol = candidateSnapshot.child("symbolName").getValue(String.class);
                    long votes = candidateSnapshot.child("votes").getValue(Long.class);

                    barEntries.add(new BarEntry(index, votes));
                    candidateSymbols.add(candidateSymbol);
                    index++;
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Votes");
                barDataSet.setColors(Color.BLUE); // Customize bar color
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(12f);

                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);

                // Set X-axis labels (candidate symbols)
                barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(candidateSymbols));
                barChart.getDescription().setText("Votes Count Of Candidates");
                barChart.getDescription().setTextSize(12f);
                barChart.invalidate(); // Refresh chart
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

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String gender = userSnapshot.child("gender").getValue(String.class);
                    String votingStatus = userSnapshot.child("votingStatus").getValue(String.class);

                    if ("Yes".equals(votingStatus)) {
                        if ("Male".equals(gender)) {
                            maleVoters++;
                        } else if ("Female".equals(gender)) {
                            femaleVoters++;
                        }
                    }
                }

                // Calculate total voters
                int totalVoters = maleVoters + femaleVoters;

                // Update the charts with the data
                updatePieCharts(totalVoters, maleVoters, femaleVoters);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Voting_Result.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePieCharts(int totalVoters, int maleVoters, int femaleVoters) {
        // Pie Chart for Total Voters (Male + Female)
        ArrayList<PieEntry> totalEntries = new ArrayList<>();
        totalEntries.add(new PieEntry(maleVoters, "Male"));
        totalEntries.add(new PieEntry(femaleVoters, "Female"));

        PieDataSet totalDataSet = new PieDataSet(totalEntries, "Total Voters");
        totalDataSet.setColors(new int[]{Color.GREEN, Color.RED}); // Blue for male, Green for female
        totalDataSet.setValueTextColor(Color.BLACK);
        totalDataSet.setValueTextSize(12f);

        PieData totalData = new PieData(totalDataSet);
        pieChartTotal.setData(totalData);
        pieChartTotal.getDescription().setText("Total Voters");
        pieChartTotal.getDescription().setTextSize(12f);
        pieChartTotal.invalidate();

        // Pie Chart for Male Voters
        ArrayList<PieEntry> maleEntries = new ArrayList<>();
        maleEntries.add(new PieEntry(maleVoters, "Male"));

        PieDataSet maleDataSet = new PieDataSet(maleEntries, "Male Voters");
        maleDataSet.setColors(new int[]{Color.GREEN});
        maleDataSet.setValueTextColor(Color.BLACK);
        maleDataSet.setValueTextSize(12f);

        PieData maleData = new PieData(maleDataSet);
        pieChartMale.setData(maleData);
        pieChartMale.getDescription().setText("Male Voters");
        pieChartMale.getDescription().setTextSize(12f);
        pieChartMale.invalidate();

        // Pie Chart for Female Voters
        ArrayList<PieEntry> femaleEntries = new ArrayList<>();
        femaleEntries.add(new PieEntry(femaleVoters, "Female"));

        PieDataSet femaleDataSet = new PieDataSet(femaleEntries, "Female Voters");
        femaleDataSet.setColors(new int[]{Color.RED});
        femaleDataSet.setValueTextColor(Color.BLACK);
        femaleDataSet.setValueTextSize(12f);

        PieData femaleData = new PieData(femaleDataSet);
        pieChartFemale.setData(femaleData);
        pieChartFemale.getDescription().setText("Female Voters");
        pieChartFemale.getDescription().setTextSize(12f);
        pieChartFemale.invalidate();
    }
}
