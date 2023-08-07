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
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
private Context context;
private List<postview> mUploads;

public  ImageAdapter(Context context1,List<postview> uploads){
    context=context1;
    mUploads=uploads;
}
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(context).inflate(R.layout.post_view,parent,false);
       return  new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
postview uploadcurrent=mUploads.get(position);
holder.txtvname.setText(uploadcurrent.getName());
        Picasso.get().load(uploadcurrent.getImageUrl()).fit().centerCrop().into(holder.simageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView txtvname;
        public ImageView simageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            txtvname=itemView.findViewById(R.id.posttext);
            simageView=itemView.findViewById(R.id.postimage);
        }
    }
}
