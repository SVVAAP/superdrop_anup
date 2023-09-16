package com.example.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.example.superdrop2.upload.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context rcontext;
    private List<Upload> mitems;
    private rest_Adapter.OnItemClickListener listener; // Add this line

    public interface OnItemClickListener {
        void onItemClick(Upload item);
    }

    // Member variable to hold the click listener
    private rest_Adapter.OnItemClickListener mListener;

    // Method to set the click listener
    public void setOnItemClickListener(rest_Adapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ImageAdapter(Context context,List<Upload> Uploads ) {
        rcontext = context;
        mitems = Uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_rv, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mitems.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.get().load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        String priceWithSymbol = "₹" + String.valueOf(uploadCurrent.getPrice()); // Add ₹ symbol
        holder.textViewPrice.setText(priceWithSymbol); // Display the price with ₹ symbol
        String PDiscount=uploadCurrent.getDiscount()+"%";
        holder.discount.setText(PDiscount);
        holder.discountprice.setText(uploadCurrent.getDiscountPrice());

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewPrice,discount,discountprice; // Add TextView for price
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.offer_name);
            imageView = itemView.findViewById(R.id.offer_img);
            textViewPrice = itemView.findViewById(R.id.offer_price);
            discount=itemView.findViewById(R.id.discount);
            discountprice=itemView.findViewById(R.id.offer_total_price);

            // Set click listener on the whole item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}