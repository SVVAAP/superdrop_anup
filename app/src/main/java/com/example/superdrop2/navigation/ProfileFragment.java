package com.example.superdrop2.navigation;

import static android.app.Activity.RESULT_OK;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.superdrop2.Admin_Activity;
import com.example.superdrop2.R;
import com.example.superdrop2.methods.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText editFullName, editPhone, editStreetAddress, editCity, editEmergencyContact;
    private RatingBar ratingBar;
    private Button submitButton, editProfileButton,admin;
    private ImageView profileImage;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private boolean isEditMode = false;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



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

        // Initialize Firebase Database and Storage References
        databaseReference = FirebaseDatabase.getInstance().getReference("customer_profiles");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void saveProfileToFirebase() {
        // Get the profile data from the EditText fields and RatingBar
        String fullName = editFullName.getText().toString();
        String phone = editPhone.getText().toString();
        String streetAddress = editStreetAddress.getText().toString();
        String city = editCity.getText().toString();
        String emergencyContact = editEmergencyContact.getText().toString();
        float rating = ratingBar.getRating();
        // Get the profile image URL if you have it

        // Create a User object and populate its attributes
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setStreetAddress(streetAddress);
        user.setCity(city);
        user.setEmergencyContact(emergencyContact);
        user.setRating(rating);
        // Set the profileImageUrl attribute using the image URL obtained

        // Convert the User object to a Map using the toMap method
        Map<String, Object> userMap = user.toMap();

        // Get the current user's ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Save the user data to Firebase using the databaseReference
            // Replace "users" with your database reference
            databaseReference.child("users").child(userId).setValue(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Profile saved successfully
                            setEditMode(false);
                            Toast.makeText(getActivity(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(getActivity(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadImageToFirebase(String profileId) {
        StorageReference imageRef = storageReference.child(profileId + ".jpg");

        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL and update the image URL in the user's profile
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                DatabaseReference profileRef = databaseReference.child(profileId);
                profileRef.child("profileImageUrl").setValue(uri.toString());
            });
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }
}
