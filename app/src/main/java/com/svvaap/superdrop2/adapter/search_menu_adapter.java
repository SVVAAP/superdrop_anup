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
import com.svvaap.superdrop2.adapter.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class search_menu_adapter extends RecyclerView.Adapter<search_menu_adapter.SearchViewholder> {
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

    public search_menu_adapter(Context context, List<Upload> Uploads) {
        rcontext = context;
        mitems = Uploads;
    }
    @NonNull
    @Override
    public search_menu_adapter.SearchViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(rcontext).inflate(R.layout.search_menu_v, parent, false);
        return new SearchViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull search_menu_adapter.SearchViewholder holder, int position) {
        Upload uploadCurrent = mitems.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.get().load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        String priceWithSymbol = "₹" + String.valueOf(uploadCurrent.getPrice()); // Add ₹ symbol
        holder.textViewPrice.setText(priceWithSymbol); // Display the price with ₹ symbol
//        String restname= "#" +String.valueOf(uploadCurrent.getRestName());
     //   holder.textViewRest_name.setText(restname);

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
    public class SearchViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewName,textViewPrice,textViewRest_name;
        public ImageView imageView;
        // Add TextView for price
        public SearchViewholder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.search_name);
            imageView = itemView.findViewById(R.id.search_img);
            textViewPrice = itemView.findViewById(R.id.search_price);
            textViewRest_name=itemView.findViewById(R.id.rest_name_text);

            // Set click listener on the whole item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
