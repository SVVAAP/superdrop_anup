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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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


        // Set up the click listener
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());
        String newstatus="Ordering";

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
        String gtotal=calculateTotalAmount();

        // Store order details in Firebase
        Order order = new Order(orderID,shippingName, shippingAddress, shippingCity,
                contactInstructions, note, paymentMethod,newstatus,gtotal);
        order.setItems(cartItemList);
        order.setUserId(userId);
        order.setDate(currentDate); // Set the current date
        order.setTime(currentTime); // Set the current time

        corderDatabaseReference.push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
        orderDatabaseReference.push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();

                // Get the owner's device token from your database
                String ownerToken = "OWNER_DEVICE_TOKEN"; // Replace with actual owner's device token

                // Create a data payload for the notification
                Map<String, String> notificationData = new HashMap<>();
                notificationData.put("title", "New Order");
                notificationData.put("message", "A new order has been placed.");

                // Send the notification to the owner using FCM
                sendNotificationToAllOwners(notificationData);
                Intent intent=new Intent(CheckoutActivity.this,OrderPlacedActivity.class);
                showNotification(CheckoutActivity.this,"New Order","Order Id:"+orderID,intent);
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
    public void showNotification(Context context,String title,String message,Intent intent){
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationid=1;
        String channelid ="Channel1";
        String channelName="My Channel";
        int importance=NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel=new NotificationChannel(channelid,channelName,importance);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder mbuilder=new NotificationCompat.Builder(context,channelid)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        PendingIntent intent1=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_MUTABLE);
        mbuilder.setContentIntent(intent1);
        notificationManager.notify(notificationid,mbuilder.build());
    }
    private void sendNotificationToAllOwners(Map<String, String> datan) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokensRef = database.getReference("tokens");

        tokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> tokens = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String token = userSnapshot.getValue(String.class);
                    if (token != null) {
                        tokens.add(token);
                    }
                }
                // Now you have all the tokens, send notifications using FCM
                sendNotification(tokens,datan);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void sendNotification(List<String> tokens, Map<String, String> data) {
        for (String token : tokens) {
            JSONObject notification = new JSONObject(data);

            JSONObject body = new JSONObject();
            try {
                body.put("to", token);
                body.put("data", notification);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String FCM_API = "https://fcm.googleapis.com/fcm/send";
            String serverKey = "YOUR_FCM_SERVER_KEY"; // Replace with your FCM server key
            String contentType = "application/json";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API, body,
                    response -> {
                        // Notification sent successfully
                    },
                    error -> {
                        // Notification sending failed
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "key=" + serverKey);
                    headers.put("Content-Type", contentType);
                    return headers;
                }
            };

            // Add the request to the request queue
            Volley.newRequestQueue(this).add(request);
        }
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
