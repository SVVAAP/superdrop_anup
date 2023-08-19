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
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public delet_Adapter(Context context, List<Upload> Uploads) {
        rcontext = context;
        mitems = Uploads;
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
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(position);
            }
        });


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
                    toggleSelection(getAdapterPosition());
                }
            });

        }
    }
    private void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position); // Notify adapter about the change
    }

    public List<Upload> getSelectedItems() {
        List<Upload> selected = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            int position = selectedItems.keyAt(i);
            selected.add(mitems.get(position));
        }
        return selected;
    }

}
