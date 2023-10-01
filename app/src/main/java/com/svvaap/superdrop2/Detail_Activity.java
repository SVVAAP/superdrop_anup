package com.svvaap.superdrop2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.svvaap.superdrop2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.svvaap.superdrop2.methods.User;
import com.svvaap.superdrop2.navigation.NavActivity;

public class Detail_Activity extends AppCompatActivity {

    private EditText fullName, phoneNumber, address, landmark, phoneNumberoptioal;
    private Spinner citySpinner;
    private Button signUpButton;
    // Firebase references
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String  Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

       String phoneNumberget = getIntent().getStringExtra("phoneNumber");


        // Initialize views
        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phone_number);
        phoneNumberoptioal=findViewById(R.id.phone_number_optioal);
        address = findViewById(R.id.address_text);
        citySpinner = findViewById(R.id.city_spinner); // Updated to use the Spinner
        landmark = findViewById(R.id.landmark_text);
        signUpButton = findViewById(R.id.signup_button);
        if(phoneNumberget!=null)
        {
            phoneNumber.setText(phoneNumberget);
            phoneNumber.setEnabled(false);
        }


        // Initialize the ArrayAdapter
        ArrayAdapter ctadapter = ArrayAdapter.createFromResource(this, R.array.city_options2, android.R.layout.simple_spinner_item);

        // Set the dropdown layout style
        ctadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        citySpinner.setAdapter(ctadapter);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all required fields are filled
                if (areFieldsValid()) {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    String iToken = task.getResult();
                                    // Store the token in Firebase Firestore
                                    Token = iToken;
                                    uploadDetails(); // Call uploadDetails() once the token is available
                                } else {
                                    Toast.makeText(Detail_Activity.this, "Failed to retrieve FCM token.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Detail_Activity.this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String name = fullName.getText().toString();
            String Phone=phoneNumber.getText().toString();
            String phone_optnl = phoneNumberoptioal.getText().toString();
            String userAddress = address.getText().toString();
            String selectedCity = citySpinner.getSelectedItem().toString(); // Get the selected city from the Spinner
            String landmarkAddress = landmark.getText().toString();

            if("Select a City".equals(selectedCity))
            {
                Toast.makeText(this, "Please Select The City", Toast.LENGTH_SHORT).show();
            }
            else {
                // Reference to the "users" node in Firebase Database
                DatabaseReference userRef = mDatabase.getReference("users").child(userId);

                // Create a User object and set the details including the selected city
                User userDetails = new User(name, Phone, phone_optnl, userAddress, selectedCity, landmarkAddress, Token);

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
        }else {
            Toast.makeText(this, "User id not found...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areFieldsValid() {
        // Check if all required fields are filled
        return !fullName.getText().toString().isEmpty()
                && !phoneNumber.getText().toString().isEmpty()
                && !phoneNumberoptioal.getText().toString().isEmpty()
                && !address.getText().toString().isEmpty()
                && !landmark.getText().toString().isEmpty()
                && citySpinner.getSelectedItemPosition() != 0;
    }
}
