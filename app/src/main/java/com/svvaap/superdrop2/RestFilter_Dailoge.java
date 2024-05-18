package com.svvaap.superdrop2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.svvaap.superdrop2.methods.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestFilter_Dailoge extends DialogFragment {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseRef;

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog window background to transparent
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public RestFilter_Dailoge() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rest_filter_dailoge, container, false);
        List<User> user = new ArrayList<>();
        restaurent_adapter adapter = new restaurent_adapter(getContext(), user);

        recyclerView = view.findViewById(R.id.rest_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("rest_users");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        User upload = postSnapshot.getValue(User.class);
                        user.add(upload);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

        return view;
    }

    public class restaurent_adapter extends RecyclerView.Adapter<restaurent_adapter.viewHolder> {
        private Context context;
        private List<User> rusers;

        public restaurent_adapter(Context context, List<User> rusers) {
            this.context = context;
            this.rusers = rusers;
        }

        @NonNull
        @Override
        public restaurent_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.rest_filter_item_v, parent, false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull restaurent_adapter.viewHolder holder, int position) {
            User current_user = rusers.get(position);
            holder.restName.setText(current_user.getRestName());
            Picasso.get().load(current_user.getRestProfileImageUrl()).fit().centerCrop().into(holder.restImg);
        }

        @Override
        public int getItemCount() {
            return rusers.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            private TextView restName;
            private ImageView restImg;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                restImg = itemView.findViewById(R.id.rest_img_h);
                restName = itemView.findViewById(R.id.rest_name_h);
            }
        }
    }
}
