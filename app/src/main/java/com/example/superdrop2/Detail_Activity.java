package com.example.superdrop2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superdrop2.methods.User;
import com.example.superdrop2.navigation.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Detail_Activity extends AppCompatActivity {

    private EditText fullName, phoneNumber, address, landmark;
    private Spinner citySpinner;
    private Button signUpButton;
    // Firebase references
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String phoneNumberget, Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        phoneNumberget = getIntent().getStringExtra("phoneNumber");

        // Initialize views
        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.address_text);
        citySpinner = findViewById(R.id.city_spinner); // Updated to use the Spinner
        landmark = findViewById(R.id.landmark_text);
        signUpButton = findViewById(R.id.signup_button);

        // Define your city options as an array of strings
        String[] cityOptions = {"Shirva", "Belman", "Nitte"};

        // Initialize the ArrayAdapter
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityOptions);

        // Set the dropdown layout style
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        citySpinner.setAdapter(cityAdapter);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String iToken = task.getResult();
                // Store the token in Firebase Firestore
                Token = iToken;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });
    }

    private void uploadDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String name = fullName.getText().toString();
            String phone_optnl = phoneNumber.getText().toString();
            String userAddress = address.getText().toString();
            String selectedCity = citySpinner.getSelectedItem().toString(); // Get the selected city from the Spinner
            String landmarkAddress = landmark.getText().toString();

            // Reference to the "users" node in Firebase Database
            DatabaseReference userRef = mDatabase.getReference("users").child(userId);

            // Create a User object and set the details including the selected city
            User userDetails = new User(name, phoneNumberget, phone_optnl, userAddress, selectedCity, landmarkAddress, Token);

            // Push the user details to the database
            userRef.setValue(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Detail_Activity.this, "Details uploaded successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Detail_Activity.this, NavActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Detail_Activity.this, "Failed to upload details.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
