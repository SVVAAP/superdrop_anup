package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartAdapter;
import com.example.superdrop2.adapter.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Cart_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        // Get the current user's unique ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve cart items from the user's cart node in the database
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);
            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cartItems.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = postSnapshot.getValue(CartItem.class);
                        cartItems.add(cartItem);
                    }
                    cartAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Cart_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case when the user is not logged in
            // You can redirect the user to the login screen or show a message
        }
    }
}