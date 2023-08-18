package com.example.superdrop2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class Cart_BottomSheet extends BottomSheetDialogFragment {
    TextView item_name, item_price, item_quantity, total_price;
    Button bt_cart,bt_order;
    ImageView item_img, plus_img, minus_img;
    int i = 1,qty;
    double price;
    String priceWithSymbol, imageUrl,itemId;
    Bitmap imageBitmap;
    byte[] data;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private FirebaseAuth mAuth;
    double totalprice;


    public Cart_BottomSheet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart__bottom_sheet, container, false);

        item_name = view.findViewById(R.id.cart_sheet_name);
        item_price = view.findViewById(R.id.cart_sheet_price);
        total_price = view.findViewById(R.id.cart_sheet_totalprice);
        item_quantity = view.findViewById(R.id.cart_sheet_quantity);
        item_img = view.findViewById(R.id.cart_sheet_img);
        bt_cart = view.findViewById(R.id.cart_sheet_updatecart_bt);
      // bt_order = view.findViewById(R.id.sheet_order_bt);
        plus_img = view.findViewById(R.id.cart_sheet_plus_bt);
        minus_img = view.findViewById(R.id.cart_sheet_minus_bt);
        mStorageRef = FirebaseStorage.getInstance().getReference("cart");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart");
        mAuth = FirebaseAuth.getInstance();

        // Retrieve item details from arguments
        Bundle args = getArguments();
        if (args != null) {
            itemId = args.getString("itemId"); // Retrieve the itemId
            String name = args.getString("name", "Default Name");
            imageUrl = args.getString("imageUrl");
            price = args.getDouble("price", 0.0);
            qty=args.getInt("quantity",1);
            i=qty;

            // Display item details in the bottom sheet
            item_name.setText(name);
            priceWithSymbol = "₹" + String.valueOf(price);
            item_price.setText(priceWithSymbol);
            total_price.setText(priceWithSymbol);
            item_quantity.setText(String.valueOf(qty));

            // Load image using Picasso or any other library you prefer
            Picasso.get().load(imageUrl).into(item_img);
        }

        plus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                item_quantity.setText(String.valueOf(i));
                totalprice=price*i;
                String withsymboltprice = "₹" + String.valueOf(price * i);
                total_price.setText(withsymboltprice);
            }
        });
        minus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i > 1) {
                    i--;
                    item_quantity.setText(String.valueOf(i));
                    totalprice=price*i;
                    String withsymboltprice = "₹" + String.valueOf(price * i);
                    total_price.setText(withsymboltprice);
                } else {
                    total_price.setText(priceWithSymbol);
                }
            }
        });// Load image using Picasso or any other library you prefer
        Picasso.get().load(imageUrl).into(item_img);
        // Inside BottomSheet.java

        bt_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = item_name.getText().toString().trim();
                double itemPrice = price;
                int quantity = i;
                String totalPriceText = total_price.getText().toString().trim();
                totalPriceText = totalPriceText.replace("₹", ""); // Remove the currency symbol
                totalprice = Double.parseDouble(totalPriceText);
                addToUserCart(itemId,itemName, imageUrl, itemPrice, quantity,totalprice);
                Toast.makeText(getActivity(), "updating..", Toast.LENGTH_SHORT).show();
            }
        });
//        bt_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getActivity(),Cart_Activity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void addToUserCart(String itemIdm, String itemName, String imageUrl, double itemPrice, int quantity, double totalPrice) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle accordingly
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("user_carts").child(userId);

        // Check if the item already exists in the cart
        userCartRef.orderByChild("itemId").equalTo(itemIdm).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Item already exists, update the quantity and total price
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = snapshot.getValue(CartItem.class);
                        snapshot.getRef().child("quantity").setValue(quantity);
                        snapshot.getRef().child("totalprice").setValue( totalPrice);
                        Toast.makeText(getActivity(), "Item quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "error...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}