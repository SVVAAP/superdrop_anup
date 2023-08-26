package com.example.superdrop_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        if (currentStatus.equals("Placed")) {
            holder.acceptButton.setText("Accept");
        } else if (currentStatus.equals("Processing")) {
            holder.acceptButton.setText("Order Processing");
        } else if (currentStatus.equals("Delivering")) {
            holder.acceptButton.setText("Order Delivering");
        } else if (currentStatus.equals("Delivered")) {
            holder.acceptButton.setText("Order Delivered");
            holder.acceptButton.setEnabled(false);
        }

        // Set click listener for the "Accept" button
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update order status here
                if (currentStatus.equals("Placed")) {
                    order.setStatus("Processing");
                    holder.acceptButton.setText("Order Processing");
                } else if (currentStatus.equals("Processing")) {
                    order.setStatus("Delivering");
                    holder.acceptButton.setText("Order Delivering");
                } else if (currentStatus.equals("Delivering")) {
                    order.setStatus("Delivered");
                    holder.acceptButton.setText("Order Delivered");
                    holder.acceptButton.setEnabled(false);
                }

                // Update the status in Firebase database
                DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
                orderDatabaseReference.child(orderId).child("status").setValue(order.getStatus());
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
        private TextView name,city,address,phone,payment,note;
        private Button acceptButton;
        private Order order;
        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            Order order;
            itemRecyclerView = itemView.findViewById(R.id.ofoodItemsRecyclerView);
            name=itemView.findViewById(R.id.oshippingNameTextView);
            city=itemView.findViewById(R.id.oshippingCityTextView);
            phone=itemView.findViewById(R.id.oshippingphoneTextView);
            address=itemView.findViewById(R.id.oshippingAddressTextView);
            payment=itemView.findViewById(R.id.opaymentMethodContentTextView);
            note=itemView.findViewById(R.id.onoteContentTextView);
            acceptButton = itemView.findViewById(R.id.oacceptButton);


            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);



        }
    }
}
