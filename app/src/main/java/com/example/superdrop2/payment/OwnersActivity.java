package com.example.superdrop2.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.superdrop2.R;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.adapter.Owner_Adapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.methods.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");

                        List<CartItem> cartItems = new ArrayList<>();
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