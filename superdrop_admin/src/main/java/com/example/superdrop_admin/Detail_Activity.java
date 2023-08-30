package com.example.superdrop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.superdrop_admin.adapter.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Detail_Activity extends AppCompatActivity {

    private EditText fullName, phoneNumber, address;
    private Button signUpButton;
    // Firebase references
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String phoneNumberget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
String userid= mAuth.getUid();
        phoneNumberget = getIntent().getStringExtra("phoneNumber");

        // Initialize views
        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.address_text);
        signUpButton = findViewById(R.id.signup_button);
        // Retrieve the device token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String ownerToken = task.getResult();
                // Store the token in Firebase Firestore
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String ownerId = userid; // Replace with actual owner ID
                DatabaseReference tokensRef = database.getReference("tokens").child(ownerId);
                tokensRef.setValue(ownerToken)
                        .addOnSuccessListener(aVoid -> {
                            // Token stored successfully
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Token Unsuccessful", Toast.LENGTH_SHORT).show();
                        });
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

            // Reference to the "users" node in Firebase Database
            DatabaseReference userRef = mDatabase.getReference("users").child(userId);

            // Create a User object and set the details
            User userDetails = new User(name,phoneNumberget, phone_optnl, userAddress);

            // Push the user details to the database
            userRef.setValue(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Detail_Activity.this, "Details uploaded successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Detail_Activity.this, OwnersActivity.class));
                        finish();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Detail_Activity.this, "Failed to upload details.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}