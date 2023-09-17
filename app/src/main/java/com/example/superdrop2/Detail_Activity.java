package com.example.superdrop2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.superdrop2.methods.User;
import com.example.superdrop2.navigation.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Detail_Activity extends AppCompatActivity {

    private EditText fullName, phoneNumber, address,city,landmark;
    private Button signUpButton;
    // Firebase references
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String phoneNumberget,Token;

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
        city = findViewById(R.id.city_text);
        landmark =findViewById(R.id.landmark_text);
        signUpButton = findViewById(R.id.signup_button);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String iToken = task.getResult();
                // Store the token in Firebase Firestore
                Token=iToken;

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
            String cityAddress = city.getText().toString();
            String landmarkAddress = landmark.getText().toString();


            // Reference to the "users" node in Firebase Database
            DatabaseReference userRef = mDatabase.getReference("users").child(userId);

            // Create a User object and set the details
            User userDetails = new User(name,phoneNumberget, phone_optnl, userAddress,cityAddress,landmarkAddress,Token);

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