package com.example.superdrop2.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.BottomSheet;
import com.example.superdrop2.R;
import com.example.superdrop2.SearchActivity;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.upload.Upload;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends Fragment {
    private String data1, data = "bunontop"; //default data
    private RecyclerView recyclerview;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private CardView card_bunontop, card_streetwok, card_bowlexpress, button_search;


    public MenuFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mUploads = new ArrayList<>();

        // Retrieve the data passed from HomeFragment
        Bundle args = getArguments();
        if (args != null) {
            data1 = args.getString("data", "bunontop");
        }
        item_view(data1);

        card_bunontop = view.findViewById(R.id.bunontop_card);
        card_streetwok = view.findViewById(R.id.streetwok_card);
        card_bowlexpress = view.findViewById(R.id.bowlexpress_card);
        button_search = view.findViewById(R.id.button2);

        recyclerview = view.findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        card_bunontop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "bunontop";
                item_view(name);
            }
        });

        card_streetwok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "streetwok";
                item_view(name);
            }
        });

        card_bowlexpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "bowlexpress";
                item_view(name);
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });


        mAdapter = new rest_Adapter(getActivity(), mUploads);
        recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new rest_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });

        return view;
    }


    public void openSearchActivity() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);

        // Apply slide-right animation
        getActivity().overridePendingTransition(R.anim.slide_right, R.anim.fade_out);

    }


    public void item_view(String rest_name) {
        if (rest_name != null) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(rest_name);
        } else {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("bunontop");
        }

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            // Inside the ValueEventListener in HomeFragment
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Get the download URL from Firebase Storage and set it in the Upload object
                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                    // Retrieve the price from Firebase and set it in the Upload object
                    Double priceValue = postSnapshot.child("price").getValue(Double.class);
                    if (priceValue != null) {
                        upload.setPrice(priceValue);
                    }
                    mUploads.add(upload);
                }
                  mAdapter.notifyDataSetChanged();
//                mAdapter = new rest_Adapter(getActivity(), mUploads);
//                recyclerview.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBottomSheetForItem(Upload item) {
        BottomSheet bottomSheetFragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("itemId", item.getItemId()); // Pass the itemId to the BottomSheet
        args.putString("name", item.getName());
        args.putString("imageUrl", item.getImageUrl());
        args.putDouble("price", item.getPrice());
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }
}

