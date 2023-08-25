package com.example.superdrop_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class foodItemAdapter extends RecyclerView.Adapter<foodItemAdapter.foodItemViewHolder> {
    private List<CartItem> cartItemList;
    private Context context;

    public foodItemAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public foodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new foodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull foodItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.cartItemName.setText(cartItem.getItemName());
        holder.cartItemPrice.setText("₹" + new DecimalFormat("0.00").format(cartItem.getItemPrice()));
        holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.CartItemTotalprice.setText("₹" + new DecimalFormat("0.00").format(cartItem.getTotalprice()));
        if (cartItem.getImageUrl() != null) {
            Picasso.get().load(cartItem.getImageUrl()).into(holder.cartItemImg);
        } else {
            Picasso.get().load(R.drawable.logo).into(holder.cartItemImg); // Replace with your default image resource
        }

    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class foodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImg;
        TextView cartItemName, cartItemPrice, cartItemQuantity, CartItemTotalprice;

        public foodItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemImg = itemView.findViewById(R.id.rest_img);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            CartItemTotalprice = itemView.findViewById(R.id.cart_total_price);
        }
    }
}