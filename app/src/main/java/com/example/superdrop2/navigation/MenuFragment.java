package com.example.superdrop2.navigation;

import static androidx.core.view.ViewGroupKt.setMargins;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.BottomSheet;
import com.example.superdrop2.R;
import com.example.superdrop2.SearchActivity;
import com.example.superdrop2.SearchFragment;
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
    private Button  button_search;
    private CardView card_bunontop, card_streetwok, card_bowlexpress;
    private FrameLayout container_search;
    private Boolean isEditMode=false;
    ImageView imageView;


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
        container_search=view.findViewById(R.id.search_container);
        imageView =view.findViewById(R.id.imageView7);

        recyclerview = view.findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));  
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SearchFragment searchFragment = new SearchFragment();
        ft.replace(R.id.search_container, searchFragment); // Use replace instead of add
        ft.addToBackStack(null); // Add to back stack to allow navigation back
        ft.commit();

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
                if (isEditMode) {
                    show(true);
                    button_search.setText("Search");
                    ViewGroup.LayoutParams params = button_search.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    button_search.setLayoutParams(params);
                    // Align the button to the center
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone((ConstraintLayout) view.getParent());
                    constraintSet.centerHorizontally(R.id.button2, ConstraintSet.PARENT_ID);
                    constraintSet.applyTo((ConstraintLayout) view.getParent());
                    button_search.animate()
                            .translationXBy(0) // Shift to right side
                            .setDuration(300) // Animation duration in milliseconds
                            .start();
                    imageView.setVisibility(View.VISIBLE);
                    isEditMode = false;
                } else {
                    show(false);
                    isEditMode = true;
                    ViewGroup.LayoutParams params = button_search.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    button_search.setLayoutParams(params);
                    button_search.setText("X");
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone((ConstraintLayout) view.getParent());
                    constraintSet.connect(R.id.button2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                    constraintSet.clear(R.id.button2, ConstraintSet.START);
                    constraintSet.applyTo((ConstraintLayout) view.getParent());
                    getActivity().overridePendingTransition(R.anim.slide_right, R.anim.fade_out);
                    imageView.setVisibility(View.GONE);
                }

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
    public void show(Boolean editMode){
        if (container_search != null) { // Add this null check
            container_search.setVisibility(editMode ? View.GONE : View.VISIBLE);
            recyclerview.setVisibility(editMode ? View.VISIBLE : View.GONE);
            card_bowlexpress.setVisibility(editMode ? View.VISIBLE : View.GONE);
            card_bunontop.setVisibility(editMode ? View.VISIBLE : View.GONE);
            card_streetwok.setVisibility(editMode ? View.VISIBLE : View.GONE);
        }
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

    public void openMenuFragment(String itemName) {
        Bundle args = new Bundle();
        args.putString("itemName", itemName);

        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(args);
        show(false);
        isEditMode = true;
        ViewGroup.LayoutParams params = button_search.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        button_search.setLayoutParams(params);
        button_search.setText("X");
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone((ConstraintLayout) view.getParent());
//        constraintSet.connect(R.id.button2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
//        constraintSet.clear(R.id.button2, ConstraintSet.START);
//        constraintSet.applyTo((ConstraintLayout) view.getParent());
        imageView.setVisibility(View.GONE);

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

