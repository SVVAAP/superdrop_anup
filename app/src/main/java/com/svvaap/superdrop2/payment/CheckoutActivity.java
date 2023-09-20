package com.svvaap.superdrop2.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.Cart_Activity;
import com.example.superdrop2.R;
import com.svvaap.superdrop2.adapter.CartAdapter;
import com.svvaap.superdrop2.adapter.CartItem;
import com.svvaap.superdrop2.methods.Order;
import com.svvaap.superdrop2.methods.User;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CheckoutActivity extends AppCompatActivity {

    private EditText shippingNameEditText, shippingAddressEditText, shippingCityEditText, shippinglandmark, contactInstructionsEditText, noteEditText,ContactOptialEditText;
    private RadioGroup paymentMethodsRadioGroup;
    private RadioButton gpayUPIRadioButton, cashOnDeliveryRadioButton;
    private TextView totalPriceTextView, deliveryCharge;
    private Button placeOrderButton,changeAddress;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private boolean isEditMode = false;
    private List<CartItem> cartItemList;
    private Spinner citySpinner;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference userCartRef, databaseReference;
    private StorageReference storageReference;
    double total = 0.0;
    private DatabaseReference orderDatabaseReference, corderDatabaseReference;
    private String userId, orderID, cToken;
    int intdeliveryCharge, ctotal;
    private ImageView back_img;

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

        shippingNameEditText = findViewById(R.id.shipping_name);
        shippingAddressEditText = findViewById(R.id.shipping_address);
        shippingCityEditText = findViewById(R.id.shipping_city);
        contactInstructionsEditText = findViewById(R.id.contact_instructions);
        shippinglandmark = findViewById(R.id.shipping_landmark);
        noteEditText = findViewById(R.id.note);
        totalPriceTextView = findViewById(R.id.checkout_grandtotal);
        citySpinner = findViewById(R.id.ccity_spinner);
        userId = currentUser.getUid();
        back_img = findViewById(R.id.backarrow_img);
        ContactOptialEditText = findViewById(R.id.contact_optional);
        userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);
        deliveryCharge = findViewById(R.id.delivery_charge);
        changeAddress = findViewById(R.id.change_address_btn);


        recyclerView = findViewById(R.id.checkout_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));

        cartItemList = new ArrayList<>();
        adapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(adapter);
        changedeliverycharge();
        setEditMode(false);
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
                    ContactOptialEditText.setText(user.getEmergencyContact());
                    cToken = user.getToken();
                    shippinglandmark.setText(user.getLandmark());
                    String selectedCityFromFirebase = user.getCity(); // Replace with the actual retrieved value

// Find the index of the selected city in the string array
                    ArrayAdapter<String> cityAdapter = (ArrayAdapter<String>) citySpinner.getAdapter();
                    int position = cityAdapter.getPosition(selectedCityFromFirebase);

// Set the selected item in the citySpinner
                    if (position != -1) {
                        citySpinner.setSelection(position);
                    } else {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(CheckoutActivity.this, "Error loading data... ", Toast.LENGTH_SHORT).show();

            }
        });

        // Initialize Firebase references
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
        corderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userId);
        // Initialize views

        ArrayAdapter ctadapter = ArrayAdapter.createFromResource(this, R.array.city_options, android.R.layout.simple_spinner_item);

        // Set the dropdown layout style
        ctadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        citySpinner.setAdapter(ctadapter);

//        paymentMethodsRadioGroup = findViewById(R.id.payment_methods_radio_group);
//        gpayUPIRadioButton = findViewById(R.id.gpay_upi);
//        cashOnDeliveryRadioButton = findViewById(R.id.cash_on_delivery);

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, Cart_Activity.class);
                startActivity(intent);
            }
        });

        placeOrderButton = findViewById(R.id.place_order_button);

        // Create a notification channel (as shown earlier)


        // Set up the click listener
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(CheckoutActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                    builder.setTitle("Place Order");
                    builder.setMessage("Conform Your Order!  \t  just for verification!");
                    builder.setIcon(R.drawable.pizza_icon);

                    // Add OK button
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked OK, perform logout
                           placeOrder();
                        }
                    });

                    // Add Cancel button
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked Cancel, do nothing
                            dialogInterface.dismiss();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    // No internet connection, display a toast message
                    Toast.makeText(CheckoutActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                } else {
                    if (isEditMode) {
                        uploaduserdetails();
                        changedeliverycharge();
                    } else {
                        setEditMode(true);
                    }
                }
            }
        });
        // delivery charge
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the delivery charge when a city is selected
                changedeliverycharge();
                calculateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

    }
    private void changedeliverycharge(){
        String selecteditem = citySpinner.getSelectedItem().toString();
        switch (selecteditem) {
            case "Shirva":
                intdeliveryCharge = 50;
                break;
            case "Belman":
            case "Mudarangadi":
                intdeliveryCharge = 100;
                break;
            case "Nitte":
            case "Kapu":
                intdeliveryCharge = 150;
                break;
            case "Moodubelle":
                intdeliveryCharge = 75;
                break;
        }
        deliveryCharge.setText("₹" + new DecimalFormat("0.00").format(intdeliveryCharge));
        total = ctotal + intdeliveryCharge;
        totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
    }
    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        shippingNameEditText.setEnabled(editMode);
        contactInstructionsEditText.setEnabled(editMode);
       shippingAddressEditText.setEnabled(editMode);
       ContactOptialEditText.setEnabled(editMode);
        shippinglandmark.setEnabled(editMode);
        citySpinner.setEnabled(editMode);
    }
    private void placeOrder() {
        orderID = generateOrderID();
        String shippingName = shippingNameEditText.getText().toString();
        String shippingAddress = shippingAddressEditText.getText().toString();
        String shippingCity = citySpinner.getSelectedItem().toString();
        String contactInstructions = contactInstructionsEditText.getText().toString();
        String note = noteEditText.getText().toString();
        String landmark = shippinglandmark.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());
        String newstatus = "Ordering";

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
        changedeliverycharge();
        // You can handle more payment methods here
        String gtotal = calculateTotalAmount();
        String orderStatus = "Pending";

        // Store order details in Firebase
        Order order = new Order(orderID, shippingName, shippingAddress, shippingCity,
                contactInstructions, note, paymentMethod, newstatus, gtotal, orderStatus, landmark);
        order.setItems(cartItemList);
        order.setUserId(userId);
        order.setDate(currentDate); // Set the current date
        order.setTime(currentTime); // Set the current time
        order.setToken(cToken);

        corderDatabaseReference.push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
        orderDatabaseReference.push().setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();

                // Get the owner's device token from your database
                String ownerToken = "OWNER_DEVICE_TOKEN"; // Replace with actual owner's device token
                sendNotificationToAllOwners();
                Intent intent = new Intent(CheckoutActivity.this, OrderPlacedActivity.class);
                showNotification(CheckoutActivity.this, "New Order", "Order Id:" + orderID, intent);
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
        double totalAmount = total + intdeliveryCharge; // Add the delivery charge to the total
        return new DecimalFormat("0.00").format(totalAmount); // Convert the total to a string
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

    public void showNotification(Context context, String title, String message, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationid = 1;
        String channelid = "Channel1";
        String channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = new NotificationChannel(channelid, channelName, importance);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context, channelid)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        PendingIntent intent1 = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE);
        mbuilder.setContentIntent(intent1);
        notificationManager.notify(notificationid, mbuilder.build());
    }

    private void sendNotificationToAllOwners() {
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
                        Log.d("Token", token);
                    }
                }
                // Now you have all the tokens, send notifications using FCM
                sendNotification(tokens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckoutActivity.this, "Token error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(List<String> tokens) {
        SendNotificationTask task = new SendNotificationTask(tokens);
        new Thread(task).start();
    }

    private class SendNotificationTask implements Runnable {
        private List<String> tokens;

        public SendNotificationTask(List<String> tokens) {
            this.tokens = tokens;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            for (String token : tokens) {
                JSONObject notification = new JSONObject();
                JSONObject body = new JSONObject();

                try {
                    notification.put("title", "New Order");
                    notification.put("body", "Order Id:" + orderID);
                    body.put("to", token);
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
                        Log.d("Notification", "Notification sending failed");
                    }
                } catch (IOException e) {
                    Log.d("error", e.toString());
                }
            }
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
                        ctotal += (cartItem.getTotalprice());
                    }
                }

                adapter.notifyDataSetChanged();
                total = ctotal + intdeliveryCharge;
                totalPriceTextView.setText("₹" + new DecimalFormat("0.00").format(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public void uploaduserdetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String name = shippingNameEditText.getText().toString();
            String phone = contactInstructionsEditText.getText().toString();
            String phone_optnl=ContactOptialEditText.getText().toString();
            String userAddress = shippingAddressEditText.getText().toString();
            String selectedCity = citySpinner.getSelectedItem().toString(); // Get the selected city from the Spinner
            String landmarkAddress = shippinglandmark.getText().toString();

            // Reference to the "users" node in Firebase Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Create a User object and set the details including the selected city
            User userDetails = new User(name, phone, phone_optnl, userAddress, selectedCity, landmarkAddress);

            // Push the user details to the database
            userRef.setValue(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CheckoutActivity.this, "Details uploaded successfully!", Toast.LENGTH_SHORT).show();
                        setEditMode(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CheckoutActivity.this, "Failed to upload details.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
