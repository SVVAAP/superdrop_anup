package com.svvaap.main_admin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.svvaap.main_admin.Adapter.User;

import java.util.ArrayList;
import java.util.List;

public class NewFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseRef;
    private List<User> userList;
    private restaurent_adapter adapter;
    private ProgressBar progressBar;

    public NewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new, container, false);

        userList = new ArrayList<>();
        adapter = new restaurent_adapter(getContext(), userList);

        recyclerView = view.findViewById(R.id.new_recyclerview);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("rest_users");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user != null) {
                        if(user.getRegistred().equals("Pending")) {
                            userList.add(user);
                        }
                    } else {
                        Log.e("NewFragment", "User is null");
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.d("NewFragment", "Data loaded, userList size: " + userList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NewFragment", "Database error: " + error.getMessage());
            }
        });

        return view;
    }

    public static class restaurent_adapter extends RecyclerView.Adapter<restaurent_adapter.viewHolder> {
        private Context context;
        private List<User> rusers;


        public restaurent_adapter(Context context, List<User> rusers) {
            this.context = context;
            this.rusers = rusers;
        }

        @NonNull
        @Override
        public restaurent_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.admi_view, parent, false);
            return new viewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull restaurent_adapter.viewHolder holder, int position) {
            User current_user = rusers.get(position);
            holder.restName.setText(current_user.getRestName());
            Picasso.get().load(current_user.getRestProfileImageUrl()).fit().centerCrop().into(holder.restImg);
            holder.name.setText(current_user.getFullName());
            holder.address.setText(current_user.getStreetAddress());
            holder.phone.setText(current_user.getPhone());
            String cStatus =current_user.getRegistred();

            if(cStatus.equals("True")){
                holder.button_constraint.setVisibility(View.GONE);
                holder.status.setText("Accepted");
                holder.status_constraint.setVisibility(View.VISIBLE);
                int Color = ContextCompat.getColor(context, android.R.color.holo_green_dark);
               holder.status_constraint.setBackgroundColor(Color);
            } else if (cStatus.equals("False")){
                holder.button_constraint.setVisibility(View.GONE);
                holder.status.setText("Declined");
                holder.status_constraint.setVisibility(View.VISIBLE);
                int Color = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                holder.status_constraint.setBackgroundColor(Color);
            }

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRegisteredStatus(current_user.getRestId(), "True",holder.progressBar);
                }
            });

            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRegisteredStatus(current_user.getRestId(), "False",holder.progressBar);
                }
            });
        }

        @Override
        public int getItemCount() {
            return rusers.size();
        }

        public static class viewHolder extends RecyclerView.ViewHolder {
            private TextView restName, name, address, phone,status;
            private ImageView restImg;
            private Button accept, decline;
            private ProgressBar progressBar;
            private ConstraintLayout status_constraint,button_constraint;

            public viewHolder(@NonNull View itemView) {
                super(itemView);
                restImg = itemView.findViewById(R.id.image_view);
                restName = itemView.findViewById(R.id.rest_name_txt);
                name = itemView.findViewById(R.id.name_txt);
                address = itemView.findViewById(R.id.address_txt);
                phone = itemView.findViewById(R.id.phone_txt);
                accept = itemView.findViewById(R.id.accept_bt);
                decline = itemView.findViewById(R.id.decline_bt);
                progressBar=itemView.findViewById(R.id.iprogress_bar);
                status=itemView.findViewById(R.id.status_txt);
                status_constraint=itemView.findViewById(R.id.status_const);
                button_constraint=itemView.findViewById(R.id.button_const);
            }
        }

        private void updateRegisteredStatus(String restId, String status,ProgressBar progressBar) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("rest_users");

            userRef.orderByChild("restId").equalTo(restId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("registred").setValue(status).addOnCompleteListener(task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Log.d("restaurent_adapter", "User registration status updated to: " + status);
                                } else {
                                    Log.e("restaurent_adapter", "Failed to update user registration status", task.getException());
                                }
                            });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.e("restaurent_adapter", "No user found with restId: " + restId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("restaurent_adapter", "Database error: " + databaseError.getMessage());
                }
            });
        }
    }
}

