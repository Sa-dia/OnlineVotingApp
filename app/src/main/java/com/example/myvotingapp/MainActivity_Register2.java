package com.example.myvotingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity_Register2 extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_NID_FILE_REQUEST = 2;

    private Uri imageUri;
    private Uri nidFileUri;

    // Firebase database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_register2);

        // Initialize Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.nameInput);
        EditText phoneInput = findViewById(R.id.phoneInput);
        EditText nidInput = findViewById(R.id.nidInput);
        EditText emailInput = findViewById(R.id.emailInput);
        Button uploadImageButton = findViewById(R.id.uploadImageButton);
        Button uploadNIDFileButton = findViewById(R.id.uploadNIDFileButton);
        Button registerButton = findViewById(R.id.registerButton);

        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        RadioButton maleRadioButton = findViewById(R.id.maleRadioButton);
        RadioButton femaleRadioButton = findViewById(R.id.femaleRadioButton);
        RadioButton otherRadioButton = findViewById(R.id.otherRadioButton);

        // Handle Upload Image button click
        uploadImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Handle Upload NID File button click
        uploadNIDFileButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_NID_FILE_REQUEST);
        });

        // Handle Register button click
        registerButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String nid = nidInput.getText().toString();
            String email = emailInput.getText().toString();

            // Get selected gender
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = null;
            if (selectedGenderId == maleRadioButton.getId()) {
                gender = "Male";
            } else if (selectedGenderId == femaleRadioButton.getId()) {
                gender = "Female";
            } else if (selectedGenderId == otherRadioButton.getId()) {
                gender = "Other";
            }

            // Check if all fields are filled
            if (name.isEmpty() || phone.isEmpty() || nid.isEmpty() || email.isEmpty() || gender == null) {
                Toast.makeText(MainActivity_Register2.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
            } else if (imageUri == null || nidFileUri == null) {
                Toast.makeText(MainActivity_Register2.this, "Please upload your image and NID file", Toast.LENGTH_SHORT).show();
            } else {
                // Create a User object
                String userId = databaseReference.push().getKey(); // Generate unique ID for the user
                User user = new User(name, phone, nid, email, gender, imageUri.toString(), nidFileUri.toString());

                // Store user data in Firebase
                databaseReference.child(userId).setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(MainActivity_Register2.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity_Register2.this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_NID_FILE_REQUEST) {
                nidFileUri = data.getData();
                Toast.makeText(this, "NID file selected successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
