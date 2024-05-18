package com.svvaap.superdrop2.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.svvaap.superdrop2.BottomSheet;
import com.svvaap.superdrop2.CatogeryFilter_Dailoge;
import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.RestFilter_Dailoge;
import com.svvaap.superdrop2.adapter.MyMenuAdapter;
import com.svvaap.superdrop2.adapter.Upload;
import com.svvaap.superdrop2.adapter.rest_Adapter;
import com.svvaap.superdrop2.adapter.search_menu_adapter;
import com.svvaap.superdrop2.methods.ezyMenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class MenuFragment extends Fragment {
    private String data1 = "bunontop"; //default data
    private RecyclerView recyclerview,mRecyclerView;
    private MyMenuAdapter myMenuAdapter;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads,mUploads2,mFilteredUploads; // List to hold filtered items
    private search_menu_adapter mFilteredAdapter,mAdapter2; ;
    private Button  button_search,filter_bt,rest_bt;
    private FrameLayout container_search;
    private Boolean isEditMode=false;
    private ImageView imageView,no_internet;
    private SearchView mSearchView;
    private LinearLayout selectedLinearLayout = null;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private  SwipeRefreshLayout swipeRefreshLayout;


    public MenuFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mUploads = new ArrayList<>();
        mUploads2 = new ArrayList<>();
        mFilteredUploads = new ArrayList<>();

        container_search=view.findViewById(R.id.search_container);
        mSearchView = view.findViewById(R.id.menu_searchView);
        recyclerview = view.findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        filter_bt=view.findViewById(R.id.filter_sheet_bt);
        rest_bt=view.findViewById(R.id.rest_sheet_bt);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        item_view_search();
        mAdapter2=new search_menu_adapter(getContext(),mUploads);
        mFilteredAdapter=new search_menu_adapter(getContext(),mFilteredUploads);
        recyclerview.setAdapter(mAdapter);
        // Retrieve the data passed from HomeFragment
        Bundle args = getArguments();
        if (args != null) {
            data1 = args.getString("data", "menu");
        }
        item_view();
        List<ezyMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new ezyMenuItem(R.drawable.hamburger, "Burger"));
        menuItems.add(new ezyMenuItem(R.drawable.fries, "Fries"));
        menuItems.add(new ezyMenuItem(R.drawable.ice_cream, "ice cream"));
        menuItems.add(new ezyMenuItem(R.drawable.soda, "Cold Drink"));
        menuItems.add(new ezyMenuItem(R.drawable.orange_juice, "Juice"));
        menuItems.add(new ezyMenuItem(R.drawable.momo, "Momos"));
        menuItems.add(new ezyMenuItem(R.drawable.noodles_1, "Noodles"));
        menuItems.add(new ezyMenuItem(R.drawable.pizza_icon, "Pizza"));
        menuItems.add(new ezyMenuItem(R.drawable.sandwich, "Sandwich"));

        // Add more menu items as needed
        mRecyclerView = view.findViewById(R.id.ezy_menu_rv);
        myMenuAdapter = new MyMenuAdapter(menuItems,this,mSearchView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mRecyclerView.setAdapter(myMenuAdapter);
        no_internet=view.findViewById(R.id.mno_internet_layout);

        //no internet check
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            no_internet.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Implement the logic to refresh your data here
                refreshData();
            }
        });
        myMenuAdapter.setOnItemClickListener(new MyMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                mSearchView.setQuery(item,true);
            }
        });

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                mSearchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
                mRecyclerView.setVisibility(View.VISIBLE);


                // Set the adapter to show all menu items when the search view is clicked
                recyclerview.setAdapter(mAdapter2);
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Restore the default menu and make the constraint visible again
                mRecyclerView.setVisibility(View.GONE);
                recyclerview.setAdapter(mAdapter);
                return false;
            }
        });
        no_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Please connect to your network!!!", Toast.LENGTH_SHORT).show();
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
        mFilteredAdapter.setOnItemClickListener(new rest_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });
        mAdapter2.setOnItemClickListener(new rest_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Dismiss the keyboard when query is submitted
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the items based on the user's search input
                if (newText == null || newText.isEmpty()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                else {
                    mRecyclerView.setVisibility(View.GONE);
                }
                filterItems(newText);
                recyclerview.setAdapter(mFilteredAdapter);
                return true;
            }
        });
        filter_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatogeryFilter_Dailoge filterBottomSheet=new CatogeryFilter_Dailoge();
                filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());

            }
         });
        rest_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestFilter_Dailoge filterBottomSheet=new RestFilter_Dailoge();
                filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());

            }
        });

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
    public void item_view_search() {
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
                    mUploads2.add(upload);
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
        args.putString("restId",item.getRestId());
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }
    private void filterItems(String query) {
        mUploads.clear();

        if (query.isEmpty()) {
            mUploads.addAll(mUploads2); // Show all items when query is empty
        } else {
            for (Upload upload : mUploads2) {
                if (upload.getName().toLowerCase().contains(query.toLowerCase())) {
                    mUploads.add(upload);
                }
            }
        }

        mFilteredAdapter.notifyDataSetChanged();
    }
    private void refreshData() {
        // Implement your data refresh logic here
        if (getView() == null) {
            return;
        }
        // For example, you can re-fetch your data from Firebase
        item_view();
        if (!isNetworkAvailable()) {
            // No internet connection, display a toast message
            no_internet.setVisibility(View.VISIBLE);
        } else {
            no_internet.setVisibility(View.GONE);
            // After data is refreshed, stop the refresh animation
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000); // Delayed for 2 seconds to simulate data loading
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public void applyFilters(String chipText, String radioText) {
        // Clear the filtered list
        mUploads.clear();

        // Perform filtering based on category and price
        for (Upload upload : mUploads2) {
            if (chipText.trim().isEmpty() || upload.getmCatogery().equalsIgnoreCase(chipText)) {
                // If category matches or no category is selected
                switch (radioText) {
                    case "0-100":
                        // Filter items in the range 0-100
                        if (upload.getPrice() >= 0 && upload.getPrice() <= 100) {
                            mUploads.add(upload);
                        }
                        break;
                    case "100-500":
                        // Filter items in the range 100-500
                        if (upload.getPrice() > 100 && upload.getPrice() <= 500) {
                            mUploads.add(upload);
                        }
                        break;
                    case "500-1000":
                        // Filter items in the range 500-1000
                        if (upload.getPrice() > 500 && upload.getPrice() <= 1000) {
                            mUploads.add(upload);
                        }
                        break;
                    case "1000-Above":
                        // Filter items above 1000
                        if (upload.getPrice() > 1000) {
                            mUploads.add(upload);
                        }
                        break;
                    default:
                        // No filtering based on price
                        mUploads.add(upload);
                        break;
                }
            }
        }

        // Notify adapter of changes
        mAdapter.notifyDataSetChanged();
    }


}