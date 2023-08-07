package com.example.superdrop2;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.upload.BunOnTopAdd_Activity;
import com.example.superdrop2.upload.Upload;
import com.example.superdrop2.upload.rest_add_Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class BottomSheet extends BottomSheetDialogFragment {
    TextView item_name,item_price,item_quantity,total_price;
    Button bt_cart,bt_order;
    ImageView item_img,plus_img,minus_img;
    int i=0;
    double price;
    String priceWithSymbol,imageUrl;
    Bitmap imageBitmap;
    byte[] data;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;


    public BottomSheet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        item_name = view.findViewById(R.id.sheet_name);
        item_price = view.findViewById(R.id.sheet_price);
        total_price = view.findViewById(R.id.sheet_totalprice);
        item_quantity = view.findViewById(R.id.sheet_quantity);
        item_img = view.findViewById(R.id.sheet_img);
        bt_cart = view.findViewById(R.id.sheet_addtocart_bt);
        bt_order = view.findViewById(R.id.sheet_order_bt);
        plus_img = view.findViewById(R.id.sheet_plus_bt);
        minus_img = view.findViewById(R.id.sheet_minus_bt);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        // Retrieve item details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "Default Name");
            imageUrl = args.getString("imageUrl");
            price = args.getDouble("price", 0.0);

            // Display item details in the bottom sheet
            item_name.setText(name);
            priceWithSymbol = "₹" + String.valueOf(price);
            item_price.setText(priceWithSymbol);
            total_price.setText(priceWithSymbol);

            // Load image using Picasso or any other library you prefer
            Picasso.get().load(imageUrl).into(item_img);
        }

        plus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                item_quantity.setText(String.valueOf(i));
                String withsymboltprice = "₹" + String.valueOf(price * i);
                total_price.setText(withsymboltprice);
            }
        });
        minus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i > 1) {
                    i--;
                    item_quantity.setText(String.valueOf(i));
                    String withsymboltprice = "₹" + String.valueOf(price * i);
                    total_price.setText(withsymboltprice);
                } else {
                    total_price.setText(priceWithSymbol);
                }
            }
        });// Load image using Picasso or any other library you prefer
        Picasso.get().load(imageUrl).into(item_img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                // After image is loaded successfully, convert it to Bitmap
                imageBitmap = ((BitmapDrawable) item_img.getDrawable()).getBitmap();
            }

            @Override
            public void onError(Exception e) {
                // Handle error if image loading fails
            }
        });
        // Inside BottomSheet.java

        return view;
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

                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();

                            // Retrieve the download URL and set it as the image URL in the Upload object
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    Upload upload = new Upload(item_name.getText().toString().trim(), downloadUri.toString(),price);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


}