package com.example.superdrop2.adapter;

import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import android.content.Context;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    public SparseBooleanArray selectedItems = new SparseBooleanArray();
    private OnItemLongClickListener longClickListener; // Interface reference
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public CartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.longClickListener = longClickListener; // Initialize the interface
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.cartItemName.setText(cartItem.getItemName());
        holder.cartItemPrice.setText("₹" + new DecimalFormat("0.00").format(cartItem.getItemPrice()));
        holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.getCartItemTotalprice.setText("₹" + new DecimalFormat("0.00").format(cartItem.getTotalprice()));
        if (cartItem.getImageUrl() != null) {
            Picasso.get().load(cartItem.getImageUrl()).into(holder.cartItemImg);
        } else {
            Picasso.get().load(R.drawable.logo).into(holder.cartItemImg); // Replace with your default image resource
        }
        if (selectedItems.get(position, false)) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.cartItemImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                toggleSelection(holder.getAdapterPosition()); // Use getAdapterPosition()
                return true;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                toggleSelection(holder.getAdapterPosition());
                return true;
            }
        });
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }
    // Get the number of selected items
   public int getSelectedItemCount() {
        return selectedItems.size();
    }

    // Get the list of selected item positions
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;

//        public void toggleSelection() {
//            // Toggle the selection state of all items
//            for (int i = 0; i < cartItemList.size(); i++) {
//                selectedItems.put(i, !selectedItems.get(i, false));
//            }
//            notifyDataSetChanged();
//        }
    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImg;
        TextView cartItemName, cartItemPrice, cartItemQuantity,getCartItemTotalprice;
        CheckBox checkBox;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemImg = itemView.findViewById(R.id.rest_img);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            getCartItemTotalprice=itemView.findViewById(R.id.cart_total_price);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
