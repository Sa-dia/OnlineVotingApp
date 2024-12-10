package com.example.myvotingapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity_verify2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_verify2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    public void goToVoterLogin(View view) {
        Intent intent = new Intent(MainActivity_verify2.this, activity_voter_login.class);
        startActivity(intent);
    }

    public void goToAdminLogin(View view) {
        Intent intent = new Intent(MainActivity_verify2.this, Admin_Login.class);
        startActivity(intent);
    }

    public void goToCandidateRegister(View view) {
        Intent intent = new Intent(MainActivity_verify2.this, Candidate_Register.class);
        startActivity(intent);
    }

}