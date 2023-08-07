package com.example.superdrop2.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.superdrop2.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder>{

    private List<String> images;

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
    }


    @Override
    public int getCount() {
        return images.size();
    }

    public class Holder extends  SliderViewAdapter.ViewHolder{

        ImageView imageView;

        public Holder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);

        }
    }

}
