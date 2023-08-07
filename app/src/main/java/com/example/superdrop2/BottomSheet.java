package com.example.superdrop2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.upload.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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


    public BottomSheet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        item_name=view.findViewById(R.id.sheet_name);
        item_price=view.findViewById(R.id.sheet_price);
        total_price=view.findViewById(R.id.sheet_totalprice);
        item_quantity=view.findViewById(R.id.sheet_quantity);
        item_img=view.findViewById(R.id.sheet_img);
        bt_cart=view.findViewById(R.id.sheet_addtocart_bt);
        bt_order=view.findViewById(R.id.sheet_order_bt);
        plus_img=view.findViewById(R.id.sheet_plus_bt);
        minus_img=view.findViewById(R.id.sheet_minus_bt);

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
                String withsymboltprice="₹" +String.valueOf(price*i);
                total_price.setText(withsymboltprice);
            }
        });
        minus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i > 1) {
                    i--;
                    item_quantity.setText(String.valueOf(i));
                    String withsymboltprice="₹" +String.valueOf(price*i);
                    total_price.setText(withsymboltprice);
                }
                else {
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
        bt_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);
                    String cartItemId = cartRef.push().getKey();

                    // Create a new Upload object to store item details
                    Upload upload = new Upload();
                    upload.setName(item_name.getText().toString());
                    upload.setPrice(price);

                    // Upload the image to Firebase Storage and get the download URL
                    // Replace 'your_storage_reference' with your actual storage reference
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("images/" + cartItemId + ".jpg");

                    // Assuming 'imageBitmap' is the Bitmap of the image you want to upload
                    // You can replace it with the actual Bitmap
                    // Inside bt_cart.setOnClickListener
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (imageBitmap != null) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                         data = baos.toByteArray();

                        // Continue with the image upload process...
                    } else {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                        // Handle the case when imageBitmap is null
                    }

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    upload.setImageUrl(imageUrl);

                                    // Create the CartItem object with item details and image URL
                                    CartItem cartItem = new CartItem(upload.getName(), upload.getPrice(), i, imageUrl);
                                    cartRef.child(cartItemId).setValue(cartItem);
                                    dismiss();
                                }
                            });
                        }
                    });
                } else {
                    // Handle the case when the user is not logged in
                    // You can redirect the user to the login screen or show a message
                }
            }
        });




        return view;
    }

}