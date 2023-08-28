package com.example.superdrop_admin.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

 public class Owner_Adapter extends RecyclerView.Adapter<Owner_Adapter.ViewHolder> {
    private List<Order> orderList;
    private Context context;
    public Owner_Adapter(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_item_v, parent, false);
        return new ViewHolder(view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.order = order;
        String orderId=order.getOrderId();
        String userid=order.getUserId();
        String currentStatus = order.getStatus();
        // Set default values to prevent NullPointerException
        holder.name.setText(order.getShippingName() != null ? order.getShippingName() : "N/A");
        holder.phone.setText(order.getContactInstructions() != null ? order.getContactInstructions() : "N/A");
        holder.city.setText(order.getShippingCity() != null ? order.getShippingCity() : "N/A");
        holder.address.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        holder.payment.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
        holder.note.setText(order.getNote() != null ? order.getNote() : "N/A");
        holder.fooditemadapter = new foodItemAdapter(order.getItems(), context);
        holder.itemRecyclerView.setAdapter(holder.fooditemadapter);
        holder.orderid.setText(orderId);
        String gtotal="₹"+order.getGrandTotal();
        holder.total.setText(gtotal);

        if (currentStatus != null && currentStatus.equals("Ordering"))  {
            holder.acceptButton.setText("Accept");
            holder.acceptButton.getLayoutParams().width = holder.itemView.getWidth() / 2;  // Set half width
            holder.cancelButton.setVisibility(View.VISIBLE);  // Show the cancel button
            holder.cancelButton.setEnabled(true);
            holder.cancelButton.setClickable(true);
            holder.acceptButton.setEnabled(true);
            holder.acceptButton.setClickable(true);
            int greenColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
            holder.acceptButton.setBackgroundColor(greenColor);
            int redColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
            holder.cancelButton.setBackgroundColor(redColor);
        } else if (currentStatus.equals("Order placed")) {
            holder.acceptButton.setText("Order Processing");
            holder.cancelButton.setVisibility(View.GONE);
        } else if (currentStatus.equals("Processing")) {
            holder.acceptButton.setText("Order Delivering");
            holder.cancelButton.setVisibility(View.GONE);
        } else if (currentStatus.equals("Delivering")) {
            holder.acceptButton.setText("Order Delivered");
            holder.cancelButton.setVisibility(View.GONE);
        }else if(currentStatus.equals("Delivered")) {
            holder.acceptButton.setText("Done");
            holder.cancelButton.setVisibility(View.GONE);
            updateButtonAppearance(holder,holder.acceptButton);
            int orangeColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
            holder.acceptButton.setBackgroundColor(orangeColor);
            holder.acceptButton.setClickable(false);
            holder.acceptButton.setEnabled(false);
        } else if (currentStatus.equals("Cancled")) {
            holder.cancelButton.setText("Cancled");
            updateButtonAppearance(holder,holder.cancelButton);
            int orangeColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
            holder.cancelButton.setBackgroundColor(orangeColor);
            holder.cancelButton.setClickable(false);
            holder.cancelButton.setEnabled(false);
        }


        if(currentStatus.equals("Order placed")||currentStatus.equals("Processing")||currentStatus.equals("Order Delivered")){
            updateButtonAppearance(holder,holder.acceptButton);
            holder.cancelButton.setVisibility(View.GONE);
        }
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateButtonAppearance(holder,holder.cancelButton);
                holder.acceptButton.setVisibility(View.GONE);
                String newStatus;
                newStatus = "Cancled";
                String orderId = order.getOrderId();
                DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
                DatabaseReference custOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userid);

                orderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Item already exists, update the quantity and total price
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("status").setValue(newStatus);
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
                            // Item already exists, update the quantity and total price
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("status").setValue(newStatus);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database read error
                    }
                });

                // Update the button text accordingly
            }
        });

        // Set click listener for the "Accept" button
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Update order status here
                String newStatus;
                if (order.getStatus().equals("Ordering")) {
                    newStatus = "Order placed";
                } else if (order.getStatus().equals("Order placed")){
                    newStatus = "Processing";
                } else if (order.getStatus().equals("Processing")) {
                    newStatus = "Delivering";
                } else if (order.getStatus().equals("Delivering")) {
                    newStatus = "Delivered";
                } else {
                    // Do nothing if the order status is already "Delivered"
                    return;
                }

                // Update the status in Firebase database for both "orders" and "cust_orders" nodes
                String orderId = order.getOrderId();
                DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
                DatabaseReference custOrderDatabaseReference = FirebaseDatabase.getInstance().getReference("cust_orders").child(userid);

                orderDatabaseReference.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Item already exists, update the quantity and total price
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("status").setValue(newStatus);
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
                            // Item already exists, update the quantity and total price
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("status").setValue(newStatus);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database read error
                    }
                });

                // Update the button text accordingly
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private TextView name,city,address,phone,payment,note,orderid,total;
        private Button acceptButton,cancelButton;
        private Order order;
        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            itemRecyclerView = itemView.findViewById(R.id.ofoodItemsRecyclerView);
            name=itemView.findViewById(R.id.oshippingNameTextView);
            city=itemView.findViewById(R.id.oshippingCityTextView);
            phone=itemView.findViewById(R.id.oshippingphoneTextView);
            address=itemView.findViewById(R.id.oshippingAddressTextView);
            payment=itemView.findViewById(R.id.opaymentMethodContentTextView);
            note=itemView.findViewById(R.id.onoteContentTextView);
            acceptButton = itemView.findViewById(R.id.oacceptButton);
            orderid=itemView.findViewById(R.id.shippingorderid);
            cancelButton=itemView.findViewById(R.id.ocancelButton);
            total=itemView.findViewById(R.id.oGrandTotal);


            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);



        }

    }
     private void updateButtonAppearance(ViewHolder holder,Button abutton) {
         // Set the width of the accept button to match the parent's width
         animateButtonWidthChange(holder.acceptButton, holder.acceptButton.getWidth(),
                 holder.itemView.getWidth());
         int orangeColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
         holder.acceptButton.setBackgroundColor(orangeColor);

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
}
