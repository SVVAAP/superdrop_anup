package com.example.superdrop_admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.superdrop_admin.adapter.CartItem;
import com.example.superdrop_admin.adapter.Order;
import com.example.superdrop_admin.adapter.Owner_Adapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class OldOrders extends Fragment {
    private RecyclerView orderRecyclerView;
    private Owner_Adapter orderAdapter;
    private List<Order> orderList;
    public OldOrders() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_old_orders, container, false);


        orderList = new ArrayList<>();
        orderAdapter = new Owner_Adapter(orderList, getContext());

        orderRecyclerView = view.findViewById(R.id.oldorder_recyclerview);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecyclerView.setAdapter(orderAdapter);

        // Retrieve orders from Firebase and populate the list
        retrieveOrdersFromFirebase();




        return view;
    }
    private void retrieveOrdersFromFirebase() {
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");

        orderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && !Objects.equals(order.getOrderStatus(), "Pending")){
                        // Retrieve the items associated with the order from the "items" node
                        List<CartItem> cartItems = new ArrayList<>();
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartItem cartItem = itemSnapshot.getValue(CartItem.class);
                            if (cartItem != null) {
                                cartItems.add(cartItem);
                                // Notify about the new item using a notification
                                String itemName = cartItem.getItemName(); // Replace with the actual property that holds the item name
                            }
                        }
                        // Set the retrieved cart items to the order
                        order.setItems(cartItems);
                        orderList.add(0,order);
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