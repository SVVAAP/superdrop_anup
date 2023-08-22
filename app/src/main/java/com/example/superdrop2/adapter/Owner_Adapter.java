package com.example.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView itemRecyclerView;
        private CartAdapter cartAdapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemRecyclerView = itemView.findViewById(R.id.foodItemsRecyclerView);

            // Set up the layout manager for the nested RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            itemRecyclerView.setLayoutManager(layoutManager);

            // Create and set the adapter for the nested RecyclerView
            cartAdapter = new CartAdapter(orderList.get(getAdapterPosition()).getItems(), itemView.getContext());
            itemRecyclerView.setAdapter(cartAdapter);
        }
    }
}
