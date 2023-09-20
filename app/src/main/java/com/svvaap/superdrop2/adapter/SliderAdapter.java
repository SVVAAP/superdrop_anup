package com.svvaap.superdrop2.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.superdrop2.R;
import com.svvaap.superdrop2.navigation.HomeFragment;
import com.svvaap.superdrop2.upload.Upload;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder>{

    private List<String> images;
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


    public SliderAdapter(List<String> images){

        this.images = images;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_fragment,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        String imageUrl = images.get(position);
        Glide.with(viewHolder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    HomeFragment homeFragment=new HomeFragment();
                    homeFragment.openOfferActivity();
                }
            }
        });
    }


    @Override
    public int getCount() {
        return images.size();
    }

    public class Holder extends  SliderViewAdapter.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        public Holder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);

        }
        @Override
        public void onClick(View v) {
            // Nothing to do here, we handle the click in the onBindViewHolder
        }
    }

}
