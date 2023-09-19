package com.example.superdrop2;

import static com.example.superdrop2.navigation.NavActivity.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartAdapter;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.navigation.NavActivity;
import com.example.superdrop2.payment.CheckoutActivity;
import com.example.superdrop2.payment.OrderPlacedActivity;
import com.example.superdrop2.upload.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    private TextView totalPriceTextView;
    private FirebaseAuth mAuth;
    private ImageView no_internet;
    private DatabaseReference userCartRef;
    double total = 0.0;
    private MediaPlayer mediaPlayer;
    ImageView edit_bt, back_bt;
    Button placeorder, deleteButton;
    private  SwipeRefreshLayout swipeRefreshLayout;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        updateBadgeNumber();
        deleteButton = findViewById(R.id.cart_delet);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle user not authenticated

        }

        String userId = currentUser.getUid();
        userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);
        totalPriceTextView = findViewById(R.id.cart_grandprice);
        placeorder = findViewById(R.id.cart_order);
no_internet=findViewById(R.id.cno_internet_layout);
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Cart_Activity.this));

        cartItemList = new ArrayList<>();
        adapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(adapter);

        retrieveCartItems();
        edit_bt = findViewById(R.id.edit_img);
        edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.toggleCheckboxVisibility();
            }
        });
        back_bt = findViewById(R.id.back_img);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Cart_Activity.this, NavActivity.class));
            }
        });
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
           no_internet.setVisibility(View.VISIBLE);
        }

       swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Implement the logic to refresh your data here
                refreshData();
            }
        });
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartItemList.isEmpty()) {
                    Toast.makeText(Cart_Activity.this, "Cart is empty. Add items to your cart.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Cart_Activity.this, CheckoutActivity.class));
                    finish();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedItems();
            }
        });
        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CartItem item) {
                showBottomSheetForItem(item);
            }
        });

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.addtocart_music); // Replace with your audio file

    }


    // Call this method whenever an item is added to the cart
    private void playItemAddedSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void showDeleteButton() {
        deleteButton.setVisibility(View.VISIBLE);
    }

    public void hideDeleteButton() {
        deleteButton.setVisibility(View.INVISIBLE);
    }

    private void retrieveCartItems() {
        userCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();

                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    String itemId = cartItemSnapshot.getKey(); // Get the item ID
                    CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItem.setItemId(itemId); // Set the item ID
                        cartItemList.add(cartItem);
                        total += (cartItem.getTotalprice());
                    }
                }

                adapter.notifyDataSetChanged();
                if (cartItemList.isEmpty()) {
                    totalPriceTextView.setText("₹0.00"); // Display default total
                } else {
                    totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

    private void deleteSelectedItems() {
        List<Integer> selectedItems = adapter.getSelectedItems();
        double deletedTotal = 0.0;

        // Iterate over selected items in reverse order
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            int position = selectedItems.get(i);
            CartItem cartItem = cartItemList.get(position);
            deletedTotal += cartItem.getTotalprice();
            DatabaseReference itemRef = userCartRef.child(cartItem.getItemId());
            // Remove the item from Firebase Database
            itemRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Item deleted successfully
                    Toast.makeText(Cart_Activity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Failed to delete item
                    Toast.makeText(Cart_Activity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            });

            // Remove the item from the local list
            cartItemList.remove(position);
        }

        // Update the total and selected items
        total -= deletedTotal;
        totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
        adapter.selectedItems.clear();
        adapter.notifyDataSetChanged();

        recreate();
    }
    private void showBottomSheetForItem(CartItem item) {
        Cart_BottomSheet bottomSheetFragment = new Cart_BottomSheet();
        Bundle args = new Bundle();
        args.putString("itemId", item.getItemId()); // Pass the itemId to the BottomSheet
        args.putString("name", item.getItemName());
        args.putString("imageUrl", item.getImageUrl());
        args.putDouble("price", item.getItemPrice());
        args.putInt("quantity",item.getQuantity());
        args.putDouble("totalprice",item.getTotalprice());
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
    private void updateBadgeNumber() {
        NavActivity navActivity = NavActivity.getInstance();
        if (navActivity != null) {
            navActivity.clear();
        }
    }
    private void refreshData() {
        // Implement your data refresh logic here

        // For example, you can re-fetch your data from Firebas
         retrieveCartItems();
        if (!isNetworkAvailable()) {
            // No internet connection, display a toast message
           no_internet.setVisibility(View.VISIBLE);
        } else {
          no_internet.setVisibility(View.GONE);
            // After data is refreshed, stop the refresh animation
        }
        // After data is refreshed, stop the refresh animation
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000); // Delayed for 2 seconds to simulate data loading
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}