package com.example.superdrop2.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.superdrop2.R;
import com.example.superdrop2.adapter.Owner_Adapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.methods.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnersActivity extends AppCompatActivity {
    private Owner_Adapter oAdapter;
    private RecyclerView recyclerview;
    List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners);
        recyclerview = findViewById(R.id.owner_recyclervew);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(OwnersActivity.this));

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }


                // Now you have a list of orders. You can pass this list to the RecyclerView adapter.
                // Example: adapter.setItems(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
        oAdapter = new Owner_Adapter(orderList,OwnersActivity.this);
        recyclerview.setAdapter(oAdapter);
    }
}