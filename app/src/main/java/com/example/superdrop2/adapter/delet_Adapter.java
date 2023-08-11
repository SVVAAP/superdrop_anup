package com.example.superdrop2.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.Cart_Activity;
import com.example.superdrop2.DeleteActivity;
import com.example.superdrop2.R;
import com.example.superdrop2.upload.Upload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class delet_Adapter extends RecyclerView.Adapter<delet_Adapter.RestImageVHolder>{
    private Context rcontext;
    private List<Upload> mitems;
    public SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int recyclerViewType;

    public delet_Adapter(Context context, List<Upload> Uploads,int recyclerViewType) {
        rcontext = context;
        mitems = Uploads;
        this.recyclerViewType = recyclerViewType;
    }
    @NonNull
    @Override
    public delet_Adapter.RestImageVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(rcontext).inflate(R.layout.rest_item_v, parent, false);
        return new delet_Adapter.RestImageVHolder(v,this);
    }

    @Override
    public void onBindViewHolder(@NonNull delet_Adapter.RestImageVHolder holder, int position) {
        Upload uploadCurrent = mitems.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.get().load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        String priceWithSymbol = "₹" + String.valueOf(uploadCurrent.getPrice()); // Add ₹ symbol
        holder.textViewPrice.setText(priceWithSymbol); // Display the price with ₹ symbol

    }
    public int getRecyclerViewType() {
        return recyclerViewType;
    }
    public void toggleSelection(int position) {

        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }
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

    @Override
    public int getItemCount() {
        return mitems.size();
    }

    public class RestImageVHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewPrice; // Add TextView for price
        CheckBox checkBox;
        public RestImageVHolder(@NonNull View itemView,delet_Adapter adapter) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.rest_name);
            imageView = itemView.findViewById(R.id.rest_img);
            textViewPrice = itemView.findViewById(R.id.rest_price);
            checkBox = itemView.findViewById(R.id.menucheckbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.toggleSelection(position);
                    }
                }
            });        }
    }
}
