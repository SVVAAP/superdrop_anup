package com.svvaap.superdrop2.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.Cart_Activity;
import com.svvaap.superdrop2.R;
import android.content.Context;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    public SparseBooleanArray selectedItems = new SparseBooleanArray();
    private CartAdapter.OnItemClickListener listener; // Add this line
    private boolean showCheckboxes = false; // Add this flag
    public interface OnItemClickListener {
        void onItemClick(CartItem item);
    }

    // Member variable to hold the click listener
    private CartAdapter.OnItemClickListener mListener;

    // Method to set the click listener
    public void setOnItemClickListener(CartAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public CartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;

    }
    public void setCartItems(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartItemViewHolder(view,this,context);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);


        holder.cartItemName.setText(cartItem.getItemName());
        String ItemPrice="₹" + new DecimalFormat("0.00").format(cartItem.getItemPrice());
        holder.cartItemPrice.setText(ItemPrice);
        holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        String TotalItemPrice="₹" + new DecimalFormat("0.00").format(cartItem.getTotalprice());
        holder.CartItemTotalprice.setText(TotalItemPrice);
//        double price =cartItem.getItemPrice();
//        int qty=cartItem.getQuantity();
//        double total=price*qty;
//        holder.getCartItemTotalprice.setText("₹" + new DecimalFormat("0.00").format(total));
        if (cartItem.getImageUrl() != null) {
            Picasso.get().load(cartItem.getImageUrl()).into(holder.cartItemImg);
        } else {
            Picasso.get().load(R.drawable.logo).into(holder.cartItemImg); // Replace with your default image resource
        }
        if (showCheckboxes) {
            holder.checkBox.setChecked(selectedItems.get(position, false));
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(cartItem);
                }
            }
        });

    }
    // Method to toggle checkbox visibility
    public void toggleCheckboxVisibility() {
        showCheckboxes = !showCheckboxes;
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {

        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        // Update visibility of delete button
        if (getSelectedItemCount() > 0) {
            ((Cart_Activity) context).showDeleteButton();
        } else {
            ((Cart_Activity) context).hideDeleteButton();
        }
        notifyItemChanged(position);
    }
    // Get the number of selected items
   public int getSelectedItemCount() {
        return selectedItems.size();
    }

    // Get the list of selected item positions
    // Get the list of selected item positions
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

//        public void toggleSelection() {
//            // Toggle the selection state of all items
//            for (int i = 0; i < cartItemList.size(); i++) {
//                selectedItems.put(i, !selectedItems.get(i, false));
//            }
//            notifyDataSetChanged();
//        }
  //  }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView cartItemImg;
        TextView cartItemName, cartItemPrice, cartItemQuantity,CartItemTotalprice;
        CheckBox checkBox;

        public CartItemViewHolder(@NonNull View itemView,CartAdapter adapter,Context context) {
            super(itemView);

            cartItemImg = itemView.findViewById(R.id.offer_img);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.offer_temPrice);
            cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            CartItemTotalprice=itemView.findViewById(R.id.offer_total_price);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);

            // Toggle selection when the checkbox is clicked
            if(adapter.showCheckboxes){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            adapter.toggleSelection(position);
                        }
                    }
                });
            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.toggleSelection(position);
                    }
                }
            });

            // Toggle selection when the item view is long-clicked
        }
        @Override
        public void onClick(View v) {
            // Nothing to do here, we handle the click in the onBindViewHolder

        }
    }
}
