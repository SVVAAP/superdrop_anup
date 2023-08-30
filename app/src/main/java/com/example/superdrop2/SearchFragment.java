package com.example.superdrop2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.superdrop2.adapter.MyMenuAdapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.adapter.search_menu_adapter;
import com.example.superdrop2.methods.ezyMenuItem;
import com.example.superdrop2.upload.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private RecyclerView recyclerview;
    private MyMenuAdapter myMenuAdapter;
    private search_menu_adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private Button button_search;
    private List<Upload> mFilteredUploads; // List to hold filtered items
    private search_menu_adapter mFilteredAdapter; // Adapter for filtered items
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        List<ezyMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new ezyMenuItem(R.drawable.hamburger, "Burger"));
        menuItems.add(new ezyMenuItem(R.drawable.fries, "Fries"));
        menuItems.add(new ezyMenuItem(R.drawable.ice_cream, "ice cream"));
        menuItems.add(new ezyMenuItem(R.drawable.momo, "Momos"));
        menuItems.add(new ezyMenuItem(R.drawable.noodles_1, "Noodles"));
        menuItems.add(new ezyMenuItem(R.drawable.orange_juice, "Juice"));
        menuItems.add(new ezyMenuItem(R.drawable.pizza_icon, "Pizza"));
        menuItems.add(new ezyMenuItem(R.drawable.sandwich, "Sandwich"));
        menuItems.add(new ezyMenuItem(R.drawable.soda, "Soda"));
        // Add more menu items as needed
        mRecyclerView = view.findViewById(R.id.ezy_menu_rv);
        myMenuAdapter = new MyMenuAdapter(menuItems,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setAdapter(myMenuAdapter);


        myMenuAdapter.setOnItemClickListener(new MyMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
               // openMenuFragmentsearch(item);
                Toast.makeText(getActivity(), "clicked..", Toast.LENGTH_SHORT).show();
            }
        });
        mUploads = new ArrayList<>();
        mFilteredUploads = new ArrayList<>();
        mSearchView = view.findViewById(R.id.searchView_m);
        mSearchView.getFocusable();
        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString("itemName", "");
            mSearchView.setQuery(itemName, false); // Set the item name in the SearchView
        }

        recyclerview = view.findViewById(R.id.search_recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        item_view();

        mAdapter = new search_menu_adapter(getActivity(), mUploads);

        mFilteredAdapter = new search_menu_adapter(getActivity(), mFilteredUploads);
        
        recyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new rest_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the items based on the user's search input
                filterItems(newText);
                recyclerview.setAdapter(mFilteredAdapter);
                return true;
            }
        });
// Create the filtered adapter and set it to the RecyclerView


        return view;
    }
    public void item_view() {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("menu");

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
    private void filterItems(String query) {
        mFilteredUploads.clear();

        if (query.isEmpty()) {
            mFilteredUploads.addAll(mUploads); // Show all items when query is empty
        } else {
            for (Upload upload : mUploads) {
                if (upload.getName().toLowerCase().contains(query.toLowerCase())) {
                    mFilteredUploads.add(upload);
                }
            }
        }

        mFilteredAdapter.notifyDataSetChanged();
    }

}