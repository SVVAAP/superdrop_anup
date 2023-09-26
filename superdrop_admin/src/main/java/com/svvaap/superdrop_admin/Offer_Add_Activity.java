package com.svvaap.superdrop_admin;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.superdrop_admin.R;
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
import com.svvaap.superdrop_admin.adapter.Upload;

public class Offer_Add_Activity extends AppCompatActivity {
    private Button oButtonChooseImage;
    private Button oButtonUpload;
    private ImageView oImageView;

    private StorageTask oUploadTask;

    private ProgressBar oProgressBar;
    private StorageReference oStorageRef;
    private DatabaseReference oDatabaseRef;
    private ActivityResultLauncher<Intent> mGetContentLauncher;
    private Uri oImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_add);

        oButtonChooseImage = findViewById(R.id.add_offr_img);
        oButtonUpload = findViewById(R.id.upload_offer);
        oImageView = findViewById(R.id.offr_img);
        oProgressBar = findViewById(R.id.offr_progressBar);
        oStorageRef = FirebaseStorage.getInstance().getReference("offers_poster");
        oDatabaseRef = FirebaseDatabase.getInstance().getReference("offers_poster");

        oButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                oImageUri = data.getData();
                                Picasso.get().load(oImageUri).into(oImageView);
                            }
                        }
                    }
                });
        oButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oUploadTask != null && oUploadTask.isInProgress()) {
                    Toast.makeText(Offer_Add_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
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
        if (oImageUri != null) {
            StorageReference fileReference = oStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(oImageUri));

            oUploadTask = fileReference.putFile(oImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    oProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(Offer_Add_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            // Retrieve the download URL and set it as the image URL in the Upload object
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    String uploadId = oDatabaseRef.push().getKey();
                                    Upload upload=new Upload(downloadUri.toString(),uploadId);
                                    oDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Offer_Add_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            oProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}