package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartAdapter;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.navigation.NavActivity;
import com.example.superdrop2.payment.CheckoutActivity;
import com.example.superdrop2.payment.OrderPlacedActivity;
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
    private DatabaseReference userCartRef;
    double total = 0.0;
    ImageView edit_bt, back_bt;
    Button placeorder, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
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

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Cart_Activity.this, CheckoutActivity.class));
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedItems();
            }
        });
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

        for (int position : selectedItems) {
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
        }

        // Remove the selected items from the local list
        for (int position : selectedItems) {
            cartItemList.remove(position);
        }

        // Clear the selection and update the adapter
        total -= deletedTotal; // Subtract deleted total from current total
        adapter.selectedItems.clear();
        adapter.notifyDataSetChanged();
        totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
    }
}