package com.svvaap.main_admin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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


public class OldFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseRef;
    private List<User> userList;
    private NewFragment.restaurent_adapter adapter;
    private ProgressBar progressBar;

    public OldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_old, container, false);

        userList = new ArrayList<>();
        adapter = new NewFragment.restaurent_adapter(getContext(), userList);

        recyclerView = view.findViewById(R.id.old_recyclerview);
        progressBar = view.findViewById(R.id.old_progress_bar);
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
                        if (!user.getRegistred().equals("Pending")) {
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

}