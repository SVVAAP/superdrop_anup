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
    TextView item_name, item_price, item_quantity, total_price;
    Button bt_cart, bt_order;
    ImageView item_img, plus_img, minus_img;
    int i = 1;
    double price;
    String priceWithSymbol, imageUrl;
    Bitmap imageBitmap;
    byte[] data;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    double totalprice;


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
        mStorageRef = FirebaseStorage.getInstance().getReference("cart");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart");
        mAuth = FirebaseAuth.getInstance();

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
                totalprice=price*i;
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
                    totalprice=price*i;
                    String withsymboltprice = "₹" + String.valueOf(price * i);
                    total_price.setText(withsymboltprice);
                } else {
                    total_price.setText(priceWithSymbol);
                }
            }
        });// Load image using Picasso or any other library you prefer
        Picasso.get().load(imageUrl).into(item_img);
        // Inside BottomSheet.java

        bt_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = item_name.getText().toString().trim();
                double itemPrice = price;
                int quantity = i;
                addToUserCart(itemName, imageUrl, itemPrice, quantity,totalprice);
            }
        });

        return view;
    }

    private void addToUserCart(String itemName, String imageUrl, double itemPrice, int quantity,double totalprice) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle accordingly
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);
        DatabaseReference cartItemRef = userCartRef.push();

        // Create a new CartItem object with the correct constructor
        CartItem cartItem = new CartItem(itemName, itemPrice, quantity, imageUrl,totalprice);

        cartItemRef.setValue(cartItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Item added to cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}