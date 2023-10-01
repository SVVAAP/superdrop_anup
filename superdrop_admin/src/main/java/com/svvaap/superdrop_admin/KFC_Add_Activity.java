package com.svvaap.superdrop_admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop_admin.R;
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

public class KFC_Add_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName, mEditTextPrice;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef,sStorageRef;
    private DatabaseReference mDatabaseRef,sDatabaseRef;

    private StorageTask mUploadTask;
    private ActivityResultLauncher<Intent> mGetContentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kfc_add);
        mButtonChooseImage = findViewById(R.id.kfc_img_bt);
        mButtonUpload = findViewById(R.id.kfc_upload_bt);
        mEditTextFileName = findViewById(R.id.kfc_item_name);
        mImageView = findViewById(R.id.kfc_item_img);
        mProgressBar = findViewById(R.id.kfc_progressBar);
        mEditTextPrice = findViewById(R.id.kfc_item_price);

        mStorageRef = FirebaseStorage.getInstance().getReference("KFC");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("KFC");
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
                    Toast.makeText(KFC_Add_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {// Get the price from the EditText and convert it to double
                    final double price = Double.parseDouble(mEditTextPrice.getText().toString().trim());

                    uploadFile(price);
                }
            }
        });

//        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openImagesActivity();
//            }
//        });

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

    private void uploadFile(final double price) {
        if (mImageUri != null) {
            String uploadId = mDatabaseRef.push().getKey(); // Generate a unique item ID
            StorageReference fileReference = mStorageRef.child(uploadId);
            String restname="KFC";
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

                            Toast.makeText(KFC_Add_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            // Retrieve the download URL and set it as the image URL in the Upload object
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), downloadUri.toString(), price);
                                    Upload upload2 = new Upload(mEditTextFileName.getText().toString().trim(), downloadUri.toString(), price,restname,uploadId);
                                    upload.setItemId(uploadId); // Set the unique item ID
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    sDatabaseRef.child(uploadId).setValue(upload2);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(KFC_Add_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

}