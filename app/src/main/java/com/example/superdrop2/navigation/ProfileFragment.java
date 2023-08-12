package com.example.superdrop2.navigation;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.superdrop2.Admin_Activity;
import com.example.superdrop2.OtpSendActivity;
import com.example.superdrop2.R;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.methods.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText editFullName, editPhone, editStreetAddress, editCity, editEmergencyContact;
    private RatingBar ratingBar;
    private Button submitButton, editProfileButton,admin,logout;
    private ImageView profileImage;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private boolean isEditMode = false;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityResultLauncher<Intent> mGetContentLauncher;
    private Uri mImageUri;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        admin = view.findViewById(R.id.adminbt);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Admin_Activity.class);
                startActivity(intent);
            }
        });

        // Initialize UI elements
        editFullName = view.findViewById(R.id.edit_full_name);
        editPhone = view.findViewById(R.id.edit_phone);
        editStreetAddress = view.findViewById(R.id.edit_street_address);
        editCity = view.findViewById(R.id.edit_city);
        editEmergencyContact = view.findViewById(R.id.edit_emergency_contact);
        ratingBar = view.findViewById(R.id.rating_bar);
        submitButton = view.findViewById(R.id.Submit);
        editProfileButton = view.findViewById(R.id.edit_Profile);
        profileImage = view.findViewById(R.id.profile_image);
        logout=view.findViewById(R.id.logout_bt);

        // Initialize Firebase Database and Storage References
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("users").child(userId);
        // Load user data from Firebase and populate UI elements
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    // Populate UI elements with user data
                    editFullName.setText(user.getFullName());
                    editPhone.setText(user.getPhone());
                    editStreetAddress.setText(user.getStreetAddress());
                    editCity.setText(user.getCity());
                    editEmergencyContact.setText(user.getEmergencyContact());
                    ratingBar.setRating(user.getRating());

                    // Load and display profile image using Picasso
                    if (user.getProfileImageUrl() != null) {
                        Picasso.get().load(user.getProfileImageUrl()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });

        // Set initial UI state
        setEditMode(false);

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileToFirebase();
            }
        });

        // Edit Profile button click listener
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    saveProfileToFirebase();
                } else {
                    setEditMode(true);
                }
            }
        });

        // Profile image click listener
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(), OtpSendActivity.class);
                startActivity(intent);
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
                                Picasso.get().load(mImageUri).into(profileImage);
                            }
                        }
                    }
                });

        return view;
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        editFullName.setEnabled(editMode);
        editPhone.setEnabled(editMode);
        editStreetAddress.setEnabled(editMode);
        editCity.setEnabled(editMode);
        editEmergencyContact.setEnabled(editMode);
        submitButton.setVisibility(editMode ? View.VISIBLE : View.GONE);
        editProfileButton.setText(editMode ? "Save Profile" : "Edit Profile");
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mGetContentLauncher.launch(intent);
    }


    private void saveProfileToFirebase() {
        // Get the profile data from the EditText fields and RatingBar
        String fullName = editFullName.getText().toString();
        String phone = editPhone.getText().toString();
        String streetAddress = editStreetAddress.getText().toString();
        String city = editCity.getText().toString();
        String emergencyContact = editEmergencyContact.getText().toString();
        float rating = ratingBar.getRating();
        String imageurl=profileImage.toString();

        // Get the current user's ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Save the user data to Firebase using the databaseReference
            // Replace "users" with your database reference
            User user = new User(fullName, phone, streetAddress, city, emergencyContact,rating,imageurl);
            databaseReference.setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Details uploaded", Toast.LENGTH_SHORT).show();
                            setEditMode(false);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
