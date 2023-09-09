package com.example.superdrop2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<String> mImageUris;

    public ImageAdapter(Context context, List<String> imageUris) {
        this.context = context;
        mImageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_rv, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUri = mImageUris.get(position);
        Picasso.get().load(imageUri).fit().centerCrop().into(holder.simageView);
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView simageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            simageView = itemView.findViewById(R.id.postimage);
        }
    }
}