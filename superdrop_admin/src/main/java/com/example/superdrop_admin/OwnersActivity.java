package com.example.superdrop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.adapter.CartItem;
import com.example.superdrop_admin.adapter.Owner_Adapter;
import com.example.superdrop_admin.adapter.Order;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnersActivity extends AppCompatActivity {
    private RecyclerView orderRecyclerView;
    private Owner_Adapter orderAdapter;
    private List<Order> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners);
        orderList = new ArrayList<>();
        orderAdapter = new Owner_Adapter(orderList,this);

        orderRecyclerView = findViewById(R.id.owner_recyclervew);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);

        // Retrieve orders from Firebase and populate the list
        retrieveOrdersFromFirebase();



        // Set OnClickListener on the ImageView to open Admin Activity

        ImageView postImageView = findViewById(R.id.post);
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Admin_Activity when ImageView is clicked
                Intent intent = new Intent(OwnersActivity.this, Admin_Activity.class);
                startActivity(intent);
            }
        });

    }

    private void retrieveOrdersFromFirebase() {
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");

        orderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear(); // Clear the orderList before adding new orders

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        // Retrieve the items associated with the order from the "items" node
                        List<CartItem> cartItems = new ArrayList<>();
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartItem cartItem = itemSnapshot.getValue(CartItem.class);
                            if (cartItem != null) {
                                cartItems.add(cartItem);
                            }
                        }

                        // Set the retrieved cart items to the order
                        order.setItems(cartItems);
                        orderList.add(order);
                    }
                }

                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }
}