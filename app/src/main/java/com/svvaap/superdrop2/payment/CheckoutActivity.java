package com.svvaap.superdrop2.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.messaging.FirebaseMessaging;
import com.svvaap.superdrop2.Cart_Activity;
import com.svvaap.superdrop2.R;
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
import java.util.Collections;
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
    private DatabaseReference orderDatabaseReference, corderDatabaseReference,dorderDatabaseReference;
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
                    assert user != null;
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
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("restaurant_orders");
        corderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userId);
        dorderDatabaseReference = FirebaseDatabase.getInstance().getReference("delivery_orders");
        // Initialize views

        ArrayAdapter ctadapter = ArrayAdapter.createFromResource(this, R.array.city_options, android.R.layout.simple_spinner_item);

        // Set the dropdown layout style
        ctadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        citySpinner.setAdapter(ctadapter);
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
                    Toast.makeText(CheckoutActivity.this, "No internet connection. Please check your network.. :(", Toast.LENGTH_SHORT).show();
                }else if(isEditMode){
                    Toast.makeText(CheckoutActivity.this, "Save the Address changes First.... :|", Toast.LENGTH_SHORT).show();
                }else   if (!areFieldsValid()) {
                    Toast.makeText(CheckoutActivity.this, "Invalid Data..... :|", Toast.LENGTH_SHORT).show();
                }
                else {

                    AlertDialog.Builder builder = getBuilder();

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
                    if (isEditMode && areFieldsValid()) {
                        uploaduserdetails();
                        changedeliverycharge();
                        changeAddress.setText("Change Address");
                    } else {
                        setEditMode(true);
                        changeAddress.setText("Save Changes");
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
        builder.setTitle("Place Order");
        builder.setMessage("Press ok to confirm order!!!");
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
        return builder;
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
        String dcharges="₹" + new DecimalFormat("0.00").format(intdeliveryCharge);
        deliveryCharge.setText(dcharges);
        total = ctotal + intdeliveryCharge;
        String Tcharges="₹" + new DecimalFormat("0.00").format(total);
        totalPriceTextView.setText(Tcharges);
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
        String phone_optnl = ContactOptialEditText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());
        String newstatus = "Ordering";

        String paymentMethod = "COD";

        if (TextUtils.isEmpty(cToken) || cToken == null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String iToken = task.getResult();
                            cToken = iToken;
                        }
                    });
        }
        changedeliverycharge();
        String gtotal = totalPriceTextView.getText().toString().replace("₹", "");
        String orderStatus = "Pending";

        for (CartItem cartItem : cartItemList) {
            String restaurantId = cartItem.getRestId();

            Order order = new Order(orderID, shippingName, shippingAddress, shippingCity,
                    contactInstructions, phone_optnl, note, paymentMethod, newstatus, gtotal, orderStatus, landmark);
            order.setItems(Collections.singletonList(cartItem));
            order.setUserId(userId);
            order.setDate(currentDate);
            order.setTime(currentTime);
            order.setToken(cToken);

            orderDatabaseReference.child(restaurantId).push().setValue(order);

            // Also push the order to the customer's node
            corderDatabaseReference.push().setValue(order);

            notifyRestaurantOwner(restaurantId, orderID, cartItem.getItemId());
        }

        Order order = new Order(orderID, shippingName, shippingAddress, shippingCity,
                contactInstructions, phone_optnl, note, paymentMethod, newstatus, gtotal, orderStatus, landmark);

        dorderDatabaseReference.push().setValue(order).addOnSuccessListener(unused -> {
            Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CheckoutActivity.this, OrderPlacedActivity.class);
            showNotification(CheckoutActivity.this, "New Order", "Order Id:" + orderID, intent);
            redirectToOrderPlacedPage();
        });
    }

    private void notifyRestaurantOwner(String restaurantId, String orderId, String itemId) {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("tokens").child(restaurantId);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                    String ownerToken = tokenSnapshot.getValue(String.class);
                    if (ownerToken != null) {
                        String title = "New Order Received";
                        String message = "You have received a new order with ID " + orderId + " and Item ID " + itemId + ". Please check your orders.";
                        sendNotification(title, message, ownerToken);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if necessary
            }
        });
    }

    private void sendNotification(String title, String message, String token) {
        new Thread(new SendNotificationTask(token, title, message)).start();
    }

    private static class SendNotificationTask implements Runnable {
        private String token;
        private String title;
        private String message;

        public SendNotificationTask(String token, String title, String message) {
            this.token = token;
            this.title = title;
            this.message = message;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            JSONObject notification = new JSONObject();
            JSONObject body = new JSONObject();

            try {
                notification.put("title", title);
                notification.put("body", message);
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
                    .addHeader("Authorization", "key=AAAAiMxksdE:APA91bFlTJqkD8AVZ36SbzIKPjILBIJOPLYTqgnnXFj4F7xAaO-Qi9ddV7OYxY-Me3zzMDvZC9UXrSfNi54OMfBELA_0RFcHGchf9egUoDjQFQspRCGA-ornfL_mNsXQ7W3QvViIgMtL")
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("Notification", "Notification sent successfully");
                } else {
                    Log.d("Notification", "Notification sending failed");
                }
            } catch (IOException e) {
                Log.d("Error", e.toString());
            }
        }
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
                .setContentTitle("Order processing")
                .setContentText(message)
                .setAutoCancel(true);

        PendingIntent intent1 = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE);
        mbuilder.setContentIntent(intent1);
        notificationManager.notify(notificationid, mbuilder.build());
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

            // Validate fields
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(userAddress) || TextUtils.isEmpty(selectedCity) || TextUtils.isEmpty(landmarkAddress)) {
                Toast.makeText(CheckoutActivity.this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

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

    private boolean areFieldsValid() {
        setEditMode(true);
        String name = shippingNameEditText.getText().toString().trim();
        String phone = contactInstructionsEditText.getText().toString().trim();
        String optionalPhone = ContactOptialEditText.getText().toString().trim();
        String userAddr = shippingAddressEditText.getText().toString().trim();
        String landmarkAddr = shippinglandmark.getText().toString().trim();
        boolean isValid = true;
        changeAddress.setText("Save Changes");

        if (name.isEmpty()) {
            shippingNameEditText.setError("Please enter your full name.");
            isValid = false;
        }

        if (phone.isEmpty()) {
            contactInstructionsEditText.setError("Please enter your phone number.");
            isValid = false;
        }

//        if (optionalPhone.isEmpty()) {
//            // Optional phone field, no need for an error if it's empty
//        }

        if (userAddr.isEmpty()) {
            shippingAddressEditText.setError("Please enter your address.");
            isValid = false;
        }

        if (landmarkAddr.isEmpty()) {
            shippinglandmark.setError("Please enter a landmark address.");
            isValid = false;
        }

        if (isValid) {
            setEditMode(false); // Only set to false when fields are valid
            changeAddress.setText("Change Address");
        }

        return isValid;
    }
}
