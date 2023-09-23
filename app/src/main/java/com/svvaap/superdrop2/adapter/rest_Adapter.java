package com.svvaap.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.upload.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class rest_Adapter extends RecyclerView.Adapter<rest_Adapter.RestImageVHolder> {
    private Context rcontext;
    private List<Upload> mitems;
    private OnItemClickListener listener; // Add this line

    public interface OnItemClickListener {
        void onItemClick(Upload item);
    }

    // Member variable to hold the click listener
    private OnItemClickListener mListener;

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public rest_Adapter(Context context, List<Upload> Uploads) {
        rcontext = context;
        mitems = Uploads;
    }

    @NonNull
    @Override
    public rest_Adapter.RestImageVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(rcontext).inflate(R.layout.rest_item_v, parent, false);
        return new RestImageVHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull rest_Adapter.RestImageVHolder holder, int position) {
        Upload uploadCurrent = mitems.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.get().load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        String priceWithSymbol = "₹" + String.valueOf(uploadCurrent.getPrice()); // Add ₹ symbol
        holder.textViewPrice.setText(priceWithSymbol); // Display the price with ₹ symbol

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(uploadCurrent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mitems.size();
    }
    public class RestImageVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewPrice; // Add TextView for price

        public RestImageVHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.offer_name);
            imageView = itemView.findViewById(R.id.offer_img);
            textViewPrice = itemView.findViewById(R.id.offer_total_price);

            // Set click listener on the whole item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Nothing to do here, we handle the click in the onBindViewHolder
        }
    }
}
