package com.svvaap.superdrop2.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.navigation.HomeFragment;
import com.svvaap.superdrop2.adapter.Upload;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder>{

    private List<Upload> upload;



    public SliderAdapter(List<Upload> upload){

        this.upload = upload;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_fragment,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Upload current = upload.get(position);
        Glide.with(viewHolder.itemView.getContext())
                .load(current.getImageUrl())
                .centerCrop()
                .into(viewHolder.imageView);
        viewHolder.rest_name.setText(current.getmRestName());

    }


    @Override
    public int getCount() {
        return upload.size();
    }

    public class Holder extends  SliderViewAdapter.ViewHolder {

        ImageView imageView;
        TextView rest_name;

        public Holder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            rest_name=itemView.findViewById(R.id.rest_name_txt);

        }

    }

}
