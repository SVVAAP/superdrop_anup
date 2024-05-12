package com.svvaap.superdrop_admin;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.superdrop_admin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.superdrop_admin.adapter.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.svvaap.superdrop_admin.adapter.User;

public class BunOnTopAdd_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName,mEditTextPrice;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
     private String restId;
    private StorageReference sStorageRef;
    private DatabaseReference sDatabaseRef;

    private StorageTask mUploadTask;
    private ActivityResultLauncher<Intent> mGetContentLauncher;
    private FirebaseAuth mAuth;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupFoodType;
    private String selectedCategory;
    private String selectedFoodType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bun_on_top_add);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("rest_users").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                assert user != null;
                restId=user.getRestId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mButtonChooseImage = findViewById(R.id.bun_img_bt);
        mButtonUpload = findViewById(R.id.bun_upload_bt);
        //mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.bun_item_name);
        mImageView = findViewById(R.id.bun_item_img);
        mProgressBar = findViewById(R.id.bun_progressBar);
        mEditTextPrice=findViewById(R.id.bun_item_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        radioGroupFoodType = findViewById(R.id.radioGroup_food_type);

        sStorageRef = FirebaseStorage.getInstance().getReference("menu");
        sDatabaseRef = FirebaseDatabase.getInstance().getReference("menu");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(BunOnTopAdd_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(areFieldsValid()) {
                        uploadFile();
                    }
                }
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.catogery_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
        radioGroupFoodType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                selectedFoodType = radioButton.getText().toString();
            }
        });
        // Initialize the ActivityResultLauncher here
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


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            String uploadId = sDatabaseRef.push().getKey();
            assert uploadId != null;
            StorageReference fileReference = sStorageRef.child(uploadId);
            double price = Double.parseDouble(mEditTextPrice.getText().toString().trim());

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(BunOnTopAdd_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            // Retrieve the download URL and set it as the image URL in the Upload object
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {// Generate a unique item ID
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), price, downloadUri.toString(), restId, uploadId, selectedCategory, selectedFoodType);
                                    sDatabaseRef.child(uploadId).setValue(upload);

                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BunOnTopAdd_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean areFieldsValid() {
        String fileName = mEditTextFileName.getText().toString().trim();
        String priceString = mEditTextPrice.getText().toString().trim();

        // Check if file name is empty
        if (fileName.isEmpty()) {
            mEditTextFileName.setError("Please enter the item name.");
            return false;
        }

        // Check if price is empty
        if (priceString.isEmpty()) {
            mEditTextPrice.setError("Please enter the item price.");
            return false;
        }

        // Check if image is selected
        if (mImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if category is selected
        if (selectedCategory == null || selectedCategory.isEmpty() || selectedCategory.equals("Select Category")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if food type is selected
        if (selectedFoodType == null || selectedFoodType.isEmpty()) {
            Toast.makeText(this, "Please select food type", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if price can be parsed to double
        try {
            double price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            mEditTextPrice.setError("Please enter a valid price.");
            return false;
        }

        return true;
    }

}