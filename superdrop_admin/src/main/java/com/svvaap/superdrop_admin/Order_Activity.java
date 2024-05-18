package com.svvaap.superdrop_admin;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.superdrop_admin.adapter.CartItem;
import com.svvaap.superdrop_admin.adapter.Order;
import com.svvaap.superdrop_admin.adapter.foodItemAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class Order_Activity extends AppCompatActivity {
private TextView name,phone,optionalphone,address,city,paymentmethord,landmark,note,total,orderid,status;
private Button acceptButton,cancelButton;
private RecyclerView recyclerView;
private Order order;
private String OrderID,cToken,currentStatus,userid,newStatus;
private foodItemAdapter fooditemadapter;
private ProgressBar progressBar;
private String restId="blank";
private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        OrderID = getIntent().getStringExtra("STRING_KEY");

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        restId = sharedPreferences.getString("restId", "blank");

        name = findViewById(R.id.order_Name);
        phone = findViewById(R.id.order_phone);
        optionalphone = findViewById(R.id.order_phone_optional);
        address = findViewById(R.id.order_Address);
        city = findViewById(R.id.order_City);
        paymentmethord = findViewById(R.id.order_paymentMethod);
        landmark = findViewById(R.id.order_Landmark);
        note = findViewById(R.id.order_note);
        total = findViewById(R.id.oredr_GrandTotal);
        acceptButton = findViewById(R.id.order_acceptButton);
        cancelButton = findViewById(R.id.order_cancelButton);
        orderid = findViewById(R.id.order_id);
        orderid.setText(OrderID);
        status=findViewById(R.id.order_status);
        progressBar = findViewById(R.id.order_progressBar);
        recyclerView = findViewById(R.id.order_foodItemsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fooditemadapter = new foodItemAdapter(new ArrayList<>(), this); // Initialize cartAdapter with an empty list
        recyclerView.setAdapter(fooditemadapter);

        retrieveOrdersFromFirebase();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(Order_Activity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    cancelButton.setEnabled(false);
                    // Update status using ExecutorService
                    newStatus = "Cancled";
                    updateStatus(newStatus, OrderID);
                    sendNotification(cToken, "Cancled", OrderID);
                }
            }
        });

        // Set click listener for the "Accept" button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(Order_Activity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    acceptButton.setEnabled(false);

                    // Determine new status
                    switch (currentStatus) {
                        case "Ordering":
                            newStatus = "Orderplaced";
                            break;
                        case "Orderplaced":
                            newStatus = "Cooking";
                            break;
                        case "Cooking":
                            newStatus = "Delivering";
                            break;
                        case "Delivering":
                            newStatus = "Delivered";
                            break;
                        default:
                            return;
                    }

                    // Execute the updateStatus in the ExecutorService
                    updateStatus(newStatus, OrderID);
                    sendNotification(cToken, newStatus, OrderID);
                    changeAppearance();
                    acceptButton.setEnabled(true);
                }
            }
        });

        changeAppearance();


    }

    private void retrieveOrdersFromFirebase() {
        // Define the order reference for the restaurant
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("restaurant_orders").child(restId);

        orderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && !Objects.equals(order.getOrderStatus(), "Pending")) {
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

                        // Check if this is the order we are looking for
                        if (Objects.equals(order.getOrderId(), OrderID)) {
                            // Populate the UI with the order details
                            name.setText(order.getShippingName());
                            phone.setText(order.getContactInstructions());
                            optionalphone.setText(order.getPhone_optnl());
                            city.setText(order.getShippingCity() != null ? order.getShippingCity() : "N/A");
                            address.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
                            paymentmethord.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
                            note.setText(order.getNote() != null ? order.getNote() : "N/A");
                            landmark.setText(order.getLandmark());
                            status.setText(order.getStatus());
                            fooditemadapter = new foodItemAdapter(order.getItems(), Order_Activity.this);
                            recyclerView.setAdapter(fooditemadapter);
                            String gtotal = "₹" + order.getGrandTotal();
                            total.setText(gtotal);
                            cToken = order.getToken();
                            currentStatus = order.getStatus();
                            userid = order.getUserId();
                            changeAppearance();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
                Log.e("Firebase", "Failed to read orders", error.toException());
            }
        });
    }
    private void updateStatus(String newStatus, String orderId) {
        try {
            // Perform your database updates here
            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
            DatabaseReference custOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userid);
            orderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                            if (Objects.equals(newStatus, "Delivered") || Objects.equals(newStatus, "Cancled")) {
                                snapshot.getRef().child("orderStatus").setValue("Done");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }
            });

            custOrderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("status").setValue(newStatus);
                            if (Objects.equals(newStatus, "Delivered") || Objects.equals(newStatus, "Cancled")) {
                                snapshot.getRef().child("orderStatus").setValue("Done");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database read error
                }
            });

            // Simulate a delay to ensure UI reflects the status change
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeAppearance();
                    progressBar.setVisibility(View.GONE);
                }
            }, 2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateButtonAppearance(Button abutton) {
        // Set the width of the accept button to match the parent's width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        animateButtonWidthChange(abutton, abutton.getWidth(), screenWidth);
        int orangeColor = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
        abutton.setBackgroundColor(orangeColor);
    }

    private void animateButtonWidthChange(Button button, int startWidth, int endWidth) {
        ValueAnimator anim = ValueAnimator.ofInt(startWidth, endWidth);
        anim.addUpdateListener(valueAnimator -> {
            int animatedValue = (int) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
            layoutParams.width = animatedValue;
            button.setLayoutParams(layoutParams);
        });

        anim.setDuration(400); // Set the duration of the animation in milliseconds
        anim.start();
    }

    private void sendNotification(String tokens, String status, String id) {
        SendNotificationTask task = new SendNotificationTask(tokens, status, id);
        new Thread(task).start();
    }

    private static class SendNotificationTask implements Runnable {
        private String tokens;
        private String status;
        private String id;

        public SendNotificationTask(String tokens, String status, String id) {
            this.tokens = tokens;
            this.status = status;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");

                JSONObject notification = new JSONObject();
                JSONObject body = new JSONObject();

                try {
                    notification.put("title", "Your Order:" + id);
                    notification.put("body", "Your order is being " + status);
                    body.put("to", tokens);
                    body.put("notification", notification);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Error", e.toString());
                }

                RequestBody requestBody = RequestBody.create(mediaType, body.toString());
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(requestBody)
                        .addHeader("Authorization", "key=AAAAiMxksdE:APA91bFlTJqkD8AVZ36SbzIKPjILBIJOPLYTqgnnXFj4F7xAaO-Qi9ddV7OYxY-Me3zzMDvZC9UXrSfNi54OMfBELA_0RFcHGchf9egUoDjQFQspRCGA-ornfL_mNsXQ7W3QvViIgMtL") // Replace with your server key
                        .addHeader("Content-Type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // Notification sent successfully
                        Log.d("Notification", "Notification sent successfully");
                    } else {
                        // Notification sending failed
                        Log.e("Notification", "Notification sending failed: " + response.body().string());
                    }
                } catch (IOException e) {
                    Log.e("Notification", "Error sending notification: " + e.toString());
                }
            } catch (Exception e) {
                Log.e("Notification", "Error sending notification: " + e.toString());
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void changeAppearance() {
        if (currentStatus != null) {
            if (currentStatus.equals("Ordering")) {
                acceptButton.setText("Accept");
                int screenWidth = getResources().getDisplayMetrics().widthPixels; // Get screen width
                acceptButton.getLayoutParams().width = screenWidth / 2;  // Set half width
                cancelButton.setVisibility(View.VISIBLE);  // Show the cancel button
                cancelButton.setEnabled(true);
                cancelButton.setClickable(true);
                acceptButton.setEnabled(true);
                acceptButton.setClickable(true);
                int greenColor = ContextCompat.getColor(this, android.R.color.holo_green_light);
                acceptButton.setBackgroundColor(greenColor);
                int redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
                cancelButton.setBackgroundColor(redColor);
            } else if (currentStatus.equals("Orderplaced")) {
                acceptButton.setText("Order Processing");
                cancelButton.setVisibility(View.GONE);
            } else if (currentStatus.equals("Cooking")) {
                acceptButton.setText("Order Delivering");
                cancelButton.setVisibility(View.GONE);
            } else if (currentStatus.equals("Delivering")) {
                acceptButton.setText("Order Delivered");
                cancelButton.setVisibility(View.GONE);
            } else if (currentStatus.equals("Delivered")) {
                acceptButton.setText("Done");
                cancelButton.setVisibility(View.GONE);
                updateButtonAppearance(acceptButton);
                int orangeColor = ContextCompat.getColor(this, android.R.color.holo_orange_light);
                acceptButton.setBackgroundColor(orangeColor);
                acceptButton.setClickable(false);
                acceptButton.setEnabled(false);
            } else if (currentStatus.equals("Cancled")) {
                cancelButton.setText("Cancled");
                acceptButton.setVisibility(View.GONE);
                // Set the width of the cancel button to match_parent
                ViewGroup.LayoutParams cancelButtonLayoutParams = cancelButton.getLayoutParams();
                cancelButtonLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                cancelButton.setLayoutParams(cancelButtonLayoutParams);
                int orangeColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
                cancelButton.setBackgroundColor(orangeColor);
                cancelButton.setClickable(false);
                cancelButton.setEnabled(false);
            }
        }
    }

}