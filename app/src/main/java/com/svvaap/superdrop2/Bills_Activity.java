package com.svvaap.superdrop2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.superdrop2.adapter.CartItem;
import com.svvaap.superdrop2.adapter.customers_adapter;
import com.svvaap.superdrop2.adapter.customers_adapter_2;
import com.svvaap.superdrop2.methods.Order;

import java.util.ArrayList;
import java.util.List;

public class Bills_Activity extends AppCompatActivity {
    private RecyclerView orderRecyclerView;
    private customers_adapter_2 orderAdapter;
    private List<Order> orderList;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bills);
        orderList = new ArrayList<>();
        orderAdapter = new customers_adapter_2(orderList, this);

        orderRecyclerView = findViewById(R.id.coustomer_recycler_vs);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);
        progressBar=findViewById(R.id.progressBar_custs);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_custs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Implement the logic to refresh your data here
                refreshData();
            }
        });
        // Retrieve orders from Firebase and populate the list
        retrieveOrdersFromFirebase();


        // Set OnClickListener on the ImageView to open Admin Activity
    }

    private void retrieveOrdersFromFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle user not authenticated

        }

        userId = currentUser.getUid();
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("cdelivery_orders").child(userId);

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
                        orderList.add(0, order);
                    }
                }
                progressBar.setVisibility(View.GONE);
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

    private void refreshData() {
        retrieveOrdersFromFirebase();
        // Implement your data refresh logic here
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

    }
}