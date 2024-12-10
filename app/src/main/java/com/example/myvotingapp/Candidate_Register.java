package com.example.myvotingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Candidate_Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameField, emailField;
    private Button uploadImageButton, addCandidateButton;
    private ImageView candidateImageView;

    private Uri imageUri;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_register);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("candidates");

        // Initialize UI components
        nameField = findViewById(R.id.et_name);
        emailField = findViewById(R.id.et_email);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        addCandidateButton = findViewById(R.id.btn_add_candidate);
        candidateImageView = findViewById(R.id.candidateImageView);

        // Browse image button click
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        // Add candidate button click
        addCandidateButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String email = emailField.getText().toString();

            if (name.isEmpty() || email.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                convertImageToBase64AndSaveData(name, email);
            }
        });
    }

    // Open image picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                candidateImageView.setImageBitmap(bitmap); // Display selected image
                Toast.makeText(this, "Image Selected Successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Convert image to Base64 string and save candidate data to Firebase Realtime Database
    private void convertImageToBase64AndSaveData(String name, String email) {
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                // Create Candidate object with image in Base64
                Candidate candidate = new Candidate(name, email, base64Image);

                // Save candidate data to Firebase Realtime Database
                String candidateId = databaseReference.push().getKey(); // Unique ID for candidate
                databaseReference.child(candidateId).setValue(candidate)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Candidate Registered Successfully", Toast.LENGTH_SHORT).show();
                            resetForm();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error Registering Candidate", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Reset form after successful submission
    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        candidateImageView.setImageResource(0); // Reset image preview
        imageUri = null;
        Toast.makeText(this, "Form Reset Successfully", Toast.LENGTH_SHORT).show();
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Candidate class
    public static class Candidate {
        public String name, email, image;

        public Candidate() {
        }

        public Candidate(String name, String email, String image) {
            this.name = name;
            this.email = email;
            this.image = image;
        }
    }
}
