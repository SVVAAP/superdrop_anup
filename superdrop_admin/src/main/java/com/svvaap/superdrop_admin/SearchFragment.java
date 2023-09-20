package com.svvaap.superdrop_admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.svvaap.superdrop_admin.adapter.search_menu_adapter;
import com.svvaap.superdrop_admin.adapter.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private RecyclerView recyclerview;
    private search_menu_adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private Button button_search;
    private List<Upload> mFilteredUploads; // List to hold filtered items
    private search_menu_adapter mFilteredAdapter; // Adapter for filtered items
    private SearchView mSearchView;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mUploads = new ArrayList<>();
        mFilteredUploads = new ArrayList<>();
        mSearchView = view.findViewById(R.id.searchView_m);
//        mSearchView.getFocusable();
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

//        mAdapter.setOnItemClickListener(new rest_Adapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Upload item) {
//                showBottomSheetForItem(item);
//            }
//        });
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