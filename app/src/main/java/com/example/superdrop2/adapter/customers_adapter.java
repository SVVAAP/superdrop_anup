package com.example.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.example.superdrop2.methods.Order;

import java.util.ArrayList;
import java.util.List;

public class customers_adapter extends RecyclerView.Adapter<customers_adapter.ViewHolder>{
    private List<Order> orderList;
    private Context context;
    public customers_adapter(List<Order> OrderList, Context context) {
        this.orderList = OrderList;
        this.context = context;

    }
    @NonNull
    @Override
    public customers_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_v, parent, false);
        return new customers_adapter.ViewHolder(view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull customers_adapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        // Set default values to prevent NullPointerException
        holder.name.setText(order.getShippingName() != null ? order.getShippingName() : "N/A");
        holder.phone.setText(order.getContactInstructions() != null ? order.getContactInstructions() : "N/A");
        holder.city.setText(order.getShippingCity() != null ? order.getShippingCity() : "N/A");
        holder.address.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        holder.payment.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
        holder.note.setText(order.getNote() != null ? order.getNote() : "N/A");
        holder.fooditemadapter = new foodItemAdapter(order.getItems(), context);
        holder.itemRecyclerView.setAdapter(holder.fooditemadapter);
        String gtotal="₹"+order.getGrandTotal();
        holder.total.setText(gtotal);


        String currentStatus=order.getStatus();
        if (currentStatus != null && currentStatus.equals("Ordering"))  {
           holder.progressBar.setProgress(0);
        } else if (currentStatus.equals("Order placed")) {
            holder.progressBar.setProgress(25);
            holder.accepted.setVisibility(View.VISIBLE);
        } else if (currentStatus.equals("Processing")) {
            holder.progressBar.setProgress(50);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
        } else if (currentStatus.equals("Delivering")) {
            holder.progressBar.setProgress(75);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
        }else if(currentStatus.equals("Delivered")) {
            holder.progressBar.setProgress(100);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.cooking.setVisibility(View.VISIBLE);
            holder.delivering.setVisibility(View.VISIBLE);
            holder.delivered.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private ImageView processing,cooking,delivering,delivered,accepted;
        private ProgressBar progressBar;
        private TextView name,city,address,phone,payment,note,total;
        public ViewHolder(@NonNull View itemView,ViewGroup parent) {
            super(itemView);
            Order order;
            itemRecyclerView = itemView.findViewById(R.id.cfoodItemsRecyclerView);
            name=itemView.findViewById(R.id.cshippingNameTextView);
            city=itemView.findViewById(R.id.cshippingCityTextView);
            phone=itemView.findViewById(R.id.cshippingphoneTextView);
            address=itemView.findViewById(R.id.cshippingAddressTextView);
            payment=itemView.findViewById(R.id.cpaymentMethodContentTextView);
            note=itemView.findViewById(R.id.cnoteContentTextView);
            total=itemView.findViewById(R.id.cGrandTotal);
            progressBar=itemView.findViewById(R.id.c_progressBar);
            processing=itemView.findViewById(R.id.processing_img);
            cooking=itemView.findViewById(R.id.cooking_img);
            delivered=itemView.findViewById(R.id.delivered_img);
            delivering=itemView.findViewById(R.id.delivering_img);
            accepted=itemView.findViewById(R.id.accept_img);

            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);
        }
    }
}
