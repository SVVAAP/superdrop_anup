package com.svvaap.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.methods.Order;

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
    public Owner_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_item_v, parent, false);
        return new Owner_Adapter.ViewHolder(view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull Owner_Adapter.ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private foodItemAdapter fooditemadapter;
        private TextView name,city,address,phone,payment,note;
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

            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            fooditemadapter = new foodItemAdapter(new ArrayList<>(), itemView.getContext()); // Initialize cartAdapter with an empty list
            itemRecyclerView.setAdapter(fooditemadapter);


        }
    }
}
