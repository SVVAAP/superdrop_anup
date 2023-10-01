package com.svvaap.superdrop2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svvaap.superdrop2.adapter.CartItem;
import com.svvaap.superdrop2.navigation.NavActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class BottomSheet extends BottomSheetDialogFragment {
    TextView item_name, item_price, item_quantity, total_price;

    ImageView item_img, plus_img, minus_img;
    private MediaPlayer mediaPlayer;
    int i = 1,newItemCount=0;
    double price;
    String priceWithSymbol, imageUrl,itemId;
    private Button bt_cart,bt_order;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    double totalprice;
    ProgressBar progressBar;


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
        bt_cart = view.findViewById(R.id.cart_sheet_updatecart_bt);
        bt_order = view.findViewById(R.id.sheet_order_bt);
        plus_img = view.findViewById(R.id.sheet_plus_bt);
        minus_img = view.findViewById(R.id.sheet_minus_bt);
        progressBar=view.findViewById(R.id.progressbar_bottom);
        progressBar.setVisibility(View.INVISIBLE);
        mStorageRef = FirebaseStorage.getInstance().getReference("cart");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart");
        mAuth = FirebaseAuth.getInstance();



        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.addtocart_music);

        bt_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Play the audio when the button is clicked
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }

                // Start the desired activity (e.g., Cart_Activity)
                Intent intent = new Intent(getActivity(), Cart_Activity.class);
                startActivity(intent);
            }
        });




        // Retrieve item details from arguments
        Bundle args = getArguments();
        if (args != null) {
            itemId = args.getString("itemId"); // Retrieve the itemId
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
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(getActivity(), "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String itemName = item_name.getText().toString().trim();
                    double itemPrice = price;
                    int quantity = i;
                    String totalPriceText = total_price.getText().toString().trim();
                    totalPriceText = totalPriceText.replace("₹", ""); // Remove the currency symbol
                    totalprice = Double.parseDouble(totalPriceText);
                    addToUserCart(itemId, itemName, imageUrl, itemPrice, quantity, totalprice);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        bt_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),Cart_Activity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void addToUserCart(String itemIdm, String itemName, String imageUrl, double itemPrice, int quantity, double totalPrice) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle accordingly
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);

        // Check if the item already exists in the cart
        userCartRef.orderByChild("itemId").equalTo(itemIdm).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Item already exists, update the quantity and total price
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = snapshot.getValue(CartItem.class);
                        int newQuantity = cartItem.getQuantity() + quantity;
                        double newTotalPrice = cartItem.getTotalprice()+totalPrice;
                        snapshot.getRef().child("quantity").setValue(newQuantity);
                        snapshot.getRef().child("totalprice").setValue(newTotalPrice);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Item quantity updated", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else {
                    // Item does not exist, add it to the cart
                    String itemId = userCartRef.push().getKey();
                    CartItem cartItem = new CartItem(itemName, itemPrice, quantity, totalPrice, imageUrl);
                    cartItem.setItemId(itemIdm);
                    userCartRef.child(itemId).setValue(cartItem)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), "Item added to cart", Toast.LENGTH_SHORT).show();
                                    newItemCount++;
                                    updateBadgeNumber(newItemCount);
                                    dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                }
                            });
                    updateBadgeNumber(newItemCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }
    // Update the badge number in NavActivity
    private void updateBadgeNumber(int newItemCount) {
        NavActivity navActivity = (NavActivity) getActivity();
        if (navActivity != null) {
            navActivity.updateBadgeNumber(newItemCount);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}