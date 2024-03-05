package com.svvaap.superdrop_admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.superdrop_admin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.svvaap.superdrop_admin.adapter.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Time;
import java.util.Calendar;

public class Detail_Activity extends AppCompatActivity {

    private EditText fullName, phoneNumber, address, rest_name, rest_city;
    private Button signUpButton;
    // Firebase references
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private ImageView mImageView;
    private Uri mImageUri;
    private ActivityResultLauncher<Intent> mGetContentLauncher;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private String phoneNumberget,ownerToken;
    private RadioGroup restaurantTypeRadioGroup;
    private RadioButton vegRadioButton, nonVegRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        String userid = mAuth.getUid();
        phoneNumberget = getIntent().getStringExtra("phoneNumber");

        // Initialize views
        fullName = findViewById(R.id.fullname);
        phoneNumber = findViewById(R.id.phone_number);
        address = findViewById(R.id.rest_address);
        signUpButton = findViewById(R.id.signup_button);
        rest_name = findViewById(R.id.rest_name); // Initialize rest_name
        rest_city = findViewById(R.id.rest_city); // Initialize rest_city
        restaurantTypeRadioGroup = findViewById(R.id.restaurant_type_radio_group);
        vegRadioButton = findViewById(R.id.rb_veg);
        nonVegRadioButton = findViewById(R.id.rb_nonveg);
        mImageView = findViewById(R.id.rest_profile);

        // Retrieve the device token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ownerToken = task.getResult();
                // Store the token in Firebase Firestore
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });

        mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                mImageUri = data.getData();
                                Picasso.get().load(mImageUri).into(mImageView);
                            }
                        }
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mGetContentLauncher.launch(intent);
    }

    private void uploadDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String name = fullName.getText().toString();
            String phone_optnl = phoneNumber.getText().toString();
            String userAddress = address.getText().toString();
            String restaurantName = rest_name.getText().toString();
            String restaurantCity = rest_city.getText().toString();
            Calendar calendar = Calendar.getInstance();
            long milliseconds = calendar.getTimeInMillis();
            String restId= "#"+restaurantName.replace(" ", "")+restaurantCity.replace(" ", "")+milliseconds;

            String restaurantType;
            int selectedRadioButtonId = restaurantTypeRadioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == vegRadioButton.getId()) {
                restaurantType = "Veg";
            } else if (selectedRadioButtonId == nonVegRadioButton.getId()) {
                restaurantType = "Non Veg";
            } else {
                // Handle if no option is selected
                restaurantType = "";
            }

            // Reference to the "users" node in Firebase Database
            DatabaseReference userRef = mDatabase.getReference("rest_users").child(userId);
            mStorageRef= FirebaseStorage.getInstance().getReference("rest_users");
            if (mImageUri != null) {
                String uploadId = userRef.push().getKey(); // Generate a unique item ID
                StorageReference fileReference = mStorageRef.child(uploadId);
                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                }, 500);

                                Toast.makeText(Detail_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                                // Retrieve the download URL and set it as the image URL in the Upload object
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        // Create a User object and set the details
                                        User userDetails = new User(name, phoneNumberget, phone_optnl, userAddress,restaurantName,restaurantCity,restaurantType,downloadUri.toString(),true,restId);

                                        // Push the user details to the database
                                        userRef.setValue(userDetails)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(Detail_Activity.this, "Details uploaded successfully!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Detail_Activity.this, OwnersTabActivity.class));
                                                    finish();

                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(Detail_Activity.this, "Failed to upload details.", Toast.LENGTH_SHORT).show();
                                                });

                                        DatabaseReference tokensRef = mDatabase.getReference("Restaurents").child(userId);
                                        User rest=new User(restId,ownerToken);
                                        tokensRef.setValue(rest);
                                    }
                                });
                            }
                        });

            }
        }
    }
}
