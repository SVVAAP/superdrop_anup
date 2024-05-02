package com.svvaap.superdrop2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svvaap.superdrop2.upload.Upload;

import java.util.List;

public class item_Adapter extends RecyclerView.Adapter<item_Adapter.itemHolder>{

    public item_Adapter(Context context, List<Upload> uploadList ){

    }
    @NonNull
    @Override
    public item_Adapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull item_Adapter.itemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        public itemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
