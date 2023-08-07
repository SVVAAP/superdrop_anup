package com.example.superdrop2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.itemName.setText(cartItem.getItemName());
        holder.itemPrice.setText(String.format("₹%.2f", cartItem.getItemPrice()));
        holder.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        Picasso.get().load(cartItem.getItemimg()).fit().centerCrop().into(holder.itemImg);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemPrice, itemQuantity;
        public ImageView itemImg;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemName);
            itemPrice = itemView.findViewById(R.id.cartItemPrice);
            itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            itemImg=itemView.findViewById(R.id.cart_img);
        }
    }
}
