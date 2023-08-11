package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.upload.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerview1,recyclerView2,recyclerView3;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads,mUploads1,mUploads2,mUploads3;
    private String bunontop,streetwok,bowlexpress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bunontop="bunontop";
        streetwok="streetwok";
        bowlexpress="bowlexpress";

        recyclerview1=findViewById(R.id.recyclerView_bunontop);
        recyclerview1.setHasFixedSize(true);
        recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2=findViewById(R.id.recyclerView_streetwok);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3=findViewById(R.id.recyclerView_bowlexpress);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));


        loadAllItems();

        Button searchButton = findViewById(R.id.search_bt);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView searchView = findViewById(R.id.searchView);
                String query = searchView.getQuery().toString();
                if (query.isEmpty()) {
                    loadAllItems();
                } else {
                    filterItems(query);
                }
            }
        });
    }
    private void loadAllItems() {
        mUploads1 = item_view(bunontop, null);
        mAdapter = new rest_Adapter(this, mUploads1);
        recyclerview1.setAdapter(mAdapter);

        mUploads2 = item_view(streetwok, null);
        mAdapter = new rest_Adapter(this, mUploads2);
        recyclerView2.setAdapter(mAdapter);

        mUploads3 = item_view(bowlexpress, null);
        mAdapter = new rest_Adapter(this, mUploads3);
        recyclerView3.setAdapter(mAdapter);
    }
    private void filterItems(String query) {
        mUploads1 = item_view(bunontop, query);
        mAdapter = new rest_Adapter(this, mUploads1);
        recyclerview1.setAdapter(mAdapter);

        mUploads2 = item_view(streetwok, query);
        mAdapter = new rest_Adapter(this, mUploads2);
        recyclerView2.setAdapter(mAdapter);

        mUploads3 = item_view(bowlexpress, query);
        mAdapter = new rest_Adapter(this, mUploads3);
        recyclerView3.setAdapter(mAdapter);
    }

    public List<Upload> item_view(String rest_name) {
        List<Upload> uploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(rest_name);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                    Double priceValue = postSnapshot.child("price").getValue(Double.class);
                    if (priceValue != null) {
                        upload.setPrice(priceValue);
                    }
                    uploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Error.... :(", Toast.LENGTH_SHORT).show();
            }
        });

        return uploads;
    }

    private void showBottomSheetForItem(Upload item) {
        BottomSheet bottomSheetFragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("itemId", item.getItemId()); // Pass the itemId to the BottomSheet
        args.putString("name", item.getName());
        args.putString("imageUrl", item.getImageUrl());
        args.putDouble("price", item.getPrice());
        bottomSheetFragment.setArguments(args);
        //bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }
    public List<Upload> item_view(String rest_name, String query) {
        List<Upload> uploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(rest_name);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Add code to filter items based on the search query
                    if (upload.getName().toLowerCase().contains(query.toLowerCase())) {
                        upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                        Double priceValue = postSnapshot.child("price").getValue(Double.class);
                        if (priceValue != null) {
                            upload.setPrice(priceValue);
                        }
                        uploads.add(upload);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Error.... :(", Toast.LENGTH_SHORT).show();
            }
        });

        return uploads;
    }
}

