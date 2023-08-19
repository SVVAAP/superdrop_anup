package com.example.superdrop2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.example.superdrop2.methods.ezyMenuItem;
import com.example.superdrop2.navigation.HomeFragment;
import com.example.superdrop2.navigation.MenuFragment;
import com.example.superdrop2.upload.Upload;

import java.util.List;

public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.ViewHolder> {

    private List<ezyMenuItem> menuItems;
    private HomeFragment homeFragment;
    private MyMenuAdapter.OnItemClickListener listener; // Add this line

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    // Member variable to hold the click listener
    private MyMenuAdapter.OnItemClickListener mListener;

    // Method to set the click listener
    public void setOnItemClickListener(MyMenuAdapter.OnItemClickListener listener) {
        mListener = listener;
    }



    public MyMenuAdapter(List<ezyMenuItem> menuItems, HomeFragment homeFragment) {
        this.menuItems = menuItems;
        this.homeFragment=homeFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ezy_access_menu_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ezyMenuItem menuItem = menuItems.get(position);
        holder.bind(menuItem);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = menuItem.getName();
                mListener.onItemClick(itemName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconImageView;
        private TextView nameTextView;
        private ezyMenuItem menuItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon);
            nameTextView = itemView.findViewById(R.id.ic_name);
            itemView.setOnClickListener(this);
        }

        public void bind(ezyMenuItem menuItem) {
            iconImageView.setImageResource(menuItem.getIconResource());
            nameTextView.setText(menuItem.getName());
        }

        @Override
        public void onClick(View v) {
            // Nothing to do here, we handle the click in the onBindViewHolder
        }


    }
}
