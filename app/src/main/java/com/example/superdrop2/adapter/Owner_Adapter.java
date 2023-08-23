package com.example.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.example.superdrop2.methods.Order;

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
    public Owner_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_item_v, parent, false);
        return new Owner_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Owner_Adapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
        holder.name.setText(order.getShippingName());
        holder.phone.setText(order.getContactInstructions());
        holder.city.setText(order.getShippingCity());
        holder.address.setText(order.getShippingAddress());
        holder.payment.setText(order.getPaymentMethod());
        holder.note.setText(order.getNote());
        holder.cartAdapter.setCartItems(order.getItems());

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
     public class ViewHolder extends RecyclerView.ViewHolder {
         private RecyclerView itemRecyclerView;
         private CartAdapter cartAdapter;
         private TextView name, city, address, phone, payment, note;

         public ViewHolder(@NonNull View itemView) {
             super(itemView);

             itemRecyclerView = itemView.findViewById(R.id.foodItemsRecyclerView);
             name = itemView.findViewById(R.id.shippingNameTextView);
             city = itemView.findViewById(R.id.shipping_city);
             phone = itemView.findViewById(R.id.shippingphoneTextView);
             address = itemView.findViewById(R.id.shippingAddressTextView);
             payment = itemView.findViewById(R.id.paymentMethodContentTextView);
             note = itemView.findViewById(R.id.noteContentTextView);

             // Set up the layout manager for the nested RecyclerView
             LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
             itemRecyclerView.setLayoutManager(layoutManager);

             // Create and set the adapter for the nested RecyclerView
             cartAdapter = new CartAdapter(null, itemView.getContext()); // Initialize with null items
             itemRecyclerView.setAdapter(cartAdapter);
         }

         public void bind(Order order) {
             name.setText(order.getShippingName());
             city.setText(order.getShippingCity());
             address.setText(order.getShippingAddress());
             payment.setText(order.getPaymentMethod());
             note.setText(order.getNote());
             cartAdapter.setCartItems(order.getItems()); // Set items for the nested RecyclerView
             cartAdapter.notifyDataSetChanged(); // Notify adapter about data change
         }
     }
 }
