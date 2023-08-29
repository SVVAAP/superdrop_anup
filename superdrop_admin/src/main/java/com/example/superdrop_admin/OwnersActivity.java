package com.example.superdrop_admin;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
    // Define the constant for notification ID
    private static final int NOTIFICATION_ID = 123; // Use any unique value you prefer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners);
        orderList = new ArrayList<>();
        orderAdapter = new Owner_Adapter(orderList, this);

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


    //notification
    private void retrieveOrdersFromFirebase() {
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");

        orderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

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
                                // Notify about the new item using a notification
                                String itemName = cartItem.getItemName(); // Replace with the actual property that holds the item name
                                showNewItemNotification(itemName);
                            }
                        }


                        // Set the retrieved cart items to the order
                        order.setItems(cartItems);
                        orderList.add(order);
                        if (order.getStatus().equals("Ordering")) {
                            // Assuming the new item's name is stored in itemName
                            String itemName = "New order"; // Replace this with the actual item name
                            showNewItemNotification(itemName);
                        }
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

// Show notification for new items

    private void showNewItemNotification(String itemName) {
        // Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.s_d_logo)
                .setContentTitle("New Item Added")
                .setContentText(itemName + " has been added to an order.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create intent for notification click
        Intent intent = new Intent(this, OwnersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}