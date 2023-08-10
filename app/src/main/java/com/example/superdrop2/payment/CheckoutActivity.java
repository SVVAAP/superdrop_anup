package com.example.superdrop2.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.Cart_Activity;
import com.example.superdrop2.R;
import com.example.superdrop2.adapter.CartAdapter;
import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.methods.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private EditText shippingNameEditText, shippingAddressEditText, shippingCityEditText,
            contactInstructionsEditText, noteEditText;

    private RadioGroup paymentMethodsRadioGroup;
    private RadioButton gpayUPIRadioButton, cashOnDeliveryRadioButton;

    private Button placeOrderButton;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    private FirebaseAuth mAuth;
    private DatabaseReference userCartRef;

    private DatabaseReference orderDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Handle user not authenticated

        }

        String userId = currentUser.getUid();
        userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);

        recyclerView = findViewById(R.id.checkout_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));

        cartItemList = new ArrayList<>();
        adapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(adapter);

        retrieveCartItems();

        // Initialize Firebase references
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize views
        shippingNameEditText = findViewById(R.id.shipping_name);
        shippingAddressEditText = findViewById(R.id.shipping_address);
        shippingCityEditText = findViewById(R.id.shipping_city);
        contactInstructionsEditText = findViewById(R.id.contact_instructions);
        noteEditText = findViewById(R.id.note);

        paymentMethodsRadioGroup = findViewById(R.id.payment_methods_radio_group);
        gpayUPIRadioButton = findViewById(R.id.gpay_upi);
        cashOnDeliveryRadioButton = findViewById(R.id.cash_on_delivery);

        placeOrderButton = findViewById(R.id.place_order_button);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void placeOrder() {
        String shippingName = shippingNameEditText.getText().toString();
        String shippingAddress = shippingAddressEditText.getText().toString();
        String shippingCity = shippingCityEditText.getText().toString();
        String contactInstructions = contactInstructionsEditText.getText().toString();
        String note = noteEditText.getText().toString();

        String paymentMethod = "";
        int selectedRadioButtonId = paymentMethodsRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == R.id.gpay_upi) {
            paymentMethod = "GPay UPI";
            // Proceed to GPay page for payment
            String totalAmount = calculateTotalAmount(); // Get the total amount from Firebase
            openGPayPayment(totalAmount);
        } else if (selectedRadioButtonId == R.id.cash_on_delivery) {
            paymentMethod = "Cash on Delivery";
            // Proceed with order placement
            placeOrderInFirebase(shippingName, shippingAddress, shippingCity, contactInstructions, note, paymentMethod);
            redirectToOrderPlacedPage();
        }

        // You can handle more payment methods here

        // Store order details in Firebase
        orderDatabaseReference.push().setValue(new Order(shippingName, shippingAddress, shippingCity,
                contactInstructions, note, paymentMethod));
    }

    private String calculateTotalAmount() {
        // Calculate and fetch the total amount from Firebase or other sources
        return "100"; // Replace with your actual total amount
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


    private void placeOrderInFirebase(String shippingName, String shippingAddress, String shippingCity,
                                      String contactInstructions, String note, String paymentMethod) {
        // Store order details in Firebase
        orderDatabaseReference.push().setValue(new Order(shippingName, shippingAddress, shippingCity,
                contactInstructions, note, paymentMethod));
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
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
            }
        });
    }

}
