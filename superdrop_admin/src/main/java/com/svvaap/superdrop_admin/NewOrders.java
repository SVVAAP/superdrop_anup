package com.svvaap.superdrop_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.superdrop_admin.R;
import com.svvaap.superdrop_admin.adapter.CartItem;
import com.svvaap.superdrop_admin.adapter.Order;
import com.svvaap.superdrop_admin.adapter.Owner_Adapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewOrders extends Fragment {
    private RecyclerView orderRecyclerView;
    private Owner_Adapter orderAdapter;
    private List<Order> orderList;

    private BackgroundMusicService backgroundMusicService;

    public NewOrders() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_orders, container, false);

        orderList = new ArrayList<>();
        orderAdapter = new Owner_Adapter(orderList, getContext());

        orderRecyclerView = view.findViewById(R.id.neworder_recyclerview);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnItemClickListener(new Owner_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String stringToPass) {
                Intent intent = new Intent(getActivity(), Order_Activity.class);
                intent.putExtra("STRING_KEY", stringToPass); // Pass the stringToPass to the new activity
                startActivity(intent);
            }
        });

        // Retrieve orders from Firebase and populate the list
        retrieveOrdersFromFirebase();

        // Initialize the service
        backgroundMusicService = new BackgroundMusicService();
        Intent serviceIntent = new Intent(getContext(), BackgroundMusicService.class);
        getContext().startService(serviceIntent);

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
                    if (order != null && Objects.equals(order.getOrderStatus(), "Pending")) {
                        // Retrieve the items associated with the order from the "items" node
                        List<CartItem> cartItems = new ArrayList<>();
                        DataSnapshot itemsSnapshot = orderSnapshot.child("items");
                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                            CartItem cartItem = itemSnapshot.getValue(CartItem.class);
                            if (cartItem != null) {
                                cartItems.add(cartItem);
                                // Notify about the new item using a notification
                            }
                        }
                        // Set the retrieved cart items to the order
                        order.setItems(cartItems);
                        orderList.add(0, order);
                    }
                }
                orderAdapter.notifyDataSetChanged();

                // Check for the order status and start/stop the background music
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && Objects.equals(order.getStatus(), "Ordering")) {
                        // Start the background music when status is "Ordering"
                        backgroundMusicService.startMusic();
                    } else {
                        // Stop the background music when status changes
                        backgroundMusicService.stopMusic();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the background music service when the fragment is destroyed
        if (backgroundMusicService != null) {
            backgroundMusicService.stopMusic();
            getContext().stopService(new Intent(getContext(), BackgroundMusicService.class));
        }
    }
}
