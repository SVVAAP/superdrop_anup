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

public class Offer_item_addActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName, mEditTextPrice,mEditTextDiscount,mEditTextDiscountPrice;
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
        setContentView(R.layout.activity_offer_item_add);

        mButtonChooseImage = findViewById(R.id.offer_img_bt);
        mButtonUpload = findViewById(R.id.offer_upload_bt);
        //mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.offer_item_name);
        mImageView = findViewById(R.id.offer_item_img);
        mProgressBar = findViewById(R.id.offer_progressBar);
        mEditTextPrice = findViewById(R.id.offer_item_price);
        mEditTextDiscount=findViewById(R.id.offer_item_discount);
        mEditTextDiscountPrice=findViewById(R.id.offer_item_discountprice);

        mStorageRef = FirebaseStorage.getInstance().getReference("Offer_item");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Offer_item");
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
                    Toast.makeText(Offer_item_addActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
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
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            String restname="BowlRxpress";
            String discount=mEditTextDiscount.getText().toString().trim();
            String discountprice=mEditTextDiscountPrice.getText().toString().trim();
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

                            Toast.makeText(Offer_item_addActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            // Retrieve the download URL and set it as the image URL in the Upload object
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    String uploadId = mDatabaseRef.push().getKey(); // Generate a unique item ID
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), downloadUri.toString(), price,restname,uploadId,discount,discountprice);
                                   // Set the unique item ID
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Offer_item_addActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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