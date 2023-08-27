package com.example.superdrop2.payment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.Cart_Activity;
import com.example.superdrop2.MainActivity;
import com.example.superdrop2.R;
import com.example.superdrop2.adapter.CartAdapter;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.methods.Order;
import com.example.superdrop2.methods.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class CheckoutActivity extends AppCompatActivity {

    private EditText shippingNameEditText, shippingAddressEditText, shippingCityEditText, contactInstructionsEditText, noteEditText;
    private RadioGroup paymentMethodsRadioGroup;
    private RadioButton gpayUPIRadioButton, cashOnDeliveryRadioButton;
    private TextView totalPriceTextView;
    private Button placeOrderButton;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    private FirebaseAuth mAuth;
    private DatabaseReference userCartRef, databaseReference;
    private StorageReference storageReference;
    double total = 0.0;
    private DatabaseReference orderDatabaseReference,corderDatabaseReference;
    String userId;

    private static final int NOTIFICATION_ID = 123; // Unique ID for the notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle user not authenticated

        }

        userId = currentUser.getUid();
        userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);


        recyclerView = findViewById(R.id.checkout_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));

        cartItemList = new ArrayList<>();
        adapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(adapter);

        retrieveCartItems();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("users").child(userId);
        // Load user data from Firebase and populate UI elements
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    // Populate UI elements with user data
                    shippingNameEditText.setText(user.getFullName());
                    contactInstructionsEditText.setText(user.getPhone());
                    shippingAddressEditText.setText(user.getStreetAddress());
                    shippingCityEditText.setText(user.getCity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(CheckoutActivity.this, "Error loading data... ", Toast.LENGTH_SHORT).show();
                ;
            }
        });

        // Initialize Firebase references
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
        corderDatabaseReference= FirebaseDatabase.getInstance().getReference("cust_orders").child(userId);
        // Initialize views
        shippingNameEditText = findViewById(R.id.shipping_name);
        shippingAddressEditText = findViewById(R.id.shipping_address);
        shippingCityEditText = findViewById(R.id.shipping_city);
        contactInstructionsEditText = findViewById(R.id.contact_instructions);
        noteEditText = findViewById(R.id.note);
        totalPriceTextView = findViewById(R.id.checkout_grandtotal);

//        paymentMethodsRadioGroup = findViewById(R.id.payment_methods_radio_group);
//        gpayUPIRadioButton = findViewById(R.id.gpay_upi);
//        cashOnDeliveryRadioButton = findViewById(R.id.cash_on_delivery);

        placeOrderButton = findViewById(R.id.place_order_button);

        // Create a notification channel (as shown earlier)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        // Set up the click listener
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderPlacedNotification();
                placeOrder();

            }
        });
    }


    // notification code here
    private void showOrderPlacedNotification() {
        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle("Order Placed")
                .setContentText("Your order has been placed successfully.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create an image Bitmap and set it to the style
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noti_icon);
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .bigPicture(imageBitmap)
                .bigLargeIcon(null); // Optional: Set a large icon for the expanded notification

        // Attach the style to the builder
        builder.setStyle(bigPictureStyle);

        // Attach an intent to open an activity when notification is clicked
        Intent intent = new Intent(this, OrderPlacedActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle permission
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    private void placeOrder() {
        String orderID = generateOrderID();
        String shippingName = shippingNameEditText.getText().toString();
        String shippingAddress = shippingAddressEditText.getText().toString();
        String shippingCity = shippingCityEditText.getText().toString();
        String contactInstructions = contactInstructionsEditText.getText().toString();
        String note = noteEditText.getText().toString();

        String paymentMethod = "COD";
//        int selectedRadioButtonId = paymentMethodsRadioGroup.getCheckedRadioButtonId();
//        if (selectedRadioButtonId == R.id.gpay_upi) {
//            paymentMethod = "GPay UPI";
//            // Proceed to GPay page for payment
//            String totalAmount = calculateTotalAmount(); // Get the total amount from Firebase
//            openGPayPayment(totalAmount);
//        } else if (selectedRadioButtonId == R.id.cash_on_delivery) {
//            paymentMethod = "Cash on Delivery";
//            // Proceed with order placement
//            placeOrderInFirebase(shippingName, shippingAddress, shippingCity, contactInstructions, note, paymentMethod);
//            redirectToOrderPlacedPage();
//        }

        // You can handle more payment methods here

        // Store order details in Firebase
        Order order = new Order(orderID,shippingName, shippingAddress, shippingCity,
                contactInstructions, note, paymentMethod);
        order.setItems(cartItemList);
        order.setUserId(userId);
        corderDatabaseReference.child(orderID).push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
        orderDatabaseReference.child(orderID).push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
                redirectToOrderPlacedPage();
            }
        });
    }
    private String generateOrderID() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        Random random = new Random();
        int randomDigits = random.nextInt(900000) + 100000; // Generate a 6-digit random number

        return timestamp + randomDigits;
    }

    private String calculateTotalAmount() {
       String grandtotal=String.valueOf(total);
        return grandtotal; // Replace with your actual total amount
    }

    private void openGPayPayment(String amount) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "8660630502@okbizaxis") // Replace with your Google Pay UPI ID
                .appendQueryParameter("pn", "svvaap")
                .appendQueryParameter("mc", "")
                .appendQueryParameter("tn", "Order Payment")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage("com.google.android.apps.nbu.paisa.user");
        startActivity(intent);
    }


    // Inside your CheckoutActivity.java

    private void redirectToOrderPlacedPage() {
        Intent intent = new Intent(this, OrderPlacedActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity if needed
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
                totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

}
