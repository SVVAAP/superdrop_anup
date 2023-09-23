package com.svvaap.superdrop2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.methods.ezyMenuItem;

import java.util.List;

public class MyMenuAdapter extends RecyclerView.Adapter<MyMenuAdapter.ViewHolder> {

    private List<ezyMenuItem> menuItems;
    private SearchView mSearchView;
    private Fragment homeFragment;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    // Member variable to hold the click listener

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }



    public MyMenuAdapter(List<ezyMenuItem> menuItems, Fragment homeFragment,SearchView searchView) {
        this.menuItems = menuItems;
        this.homeFragment=homeFragment;
        this.mSearchView = searchView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ezy_access_menu_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMenuAdapter.ViewHolder holder, int position) {
        ezyMenuItem menuItem = menuItems.get(position);
        holder.iconImageView.setImageResource(menuItem.getIconResource());
        holder.nameTextView.setText(menuItem.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = menuItem.getName();
                if (mListener != null) {
                    mListener.onItemClick(itemName);
                }
                    if (mSearchView != null) {
                    mSearchView.setQuery(itemName, true); // The second parameter submits the query
                }
            }
        });
        holder.iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = menuItem.getName();
                if (mListener != null) {
                    mListener.onItemClick(itemName);
                }
                if (mSearchView != null) {
                    mSearchView.setQuery(itemName, true); // The second parameter submits the query
                }
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon);
            nameTextView = itemView.findViewById(R.id.ic_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Nothing to do here, we handle the click in the onBindViewHolder
        }


    }
}
