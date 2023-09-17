package com.example.superdrop2.navigation;

import static androidx.core.view.ViewGroupKt.setMargins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.superdrop2.BottomSheet;
import com.example.superdrop2.R;
import com.example.superdrop2.SearchActivity;
import com.example.superdrop2.SearchFragment;
import com.example.superdrop2.adapter.MyMenuAdapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.adapter.search_menu_adapter;
import com.example.superdrop2.methods.ezyMenuItem;
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
    private String data1 = "bunontop"; //default data
    private RecyclerView recyclerview,mRecyclerView;
    private MyMenuAdapter myMenuAdapter;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads,mUploads2,mFilteredUploads; // List to hold filtered items
    private search_menu_adapter mFilteredAdapter,mAdapter2; ;
    private Button  button_search;
    private CardView card_bunontop, card_streetwok, card_bowlexpress,card_vadapavexpress,card_kfc;
    private FrameLayout container_search;
    private Boolean isEditMode=false;
    private ImageView imageView,no_internet;
    private SearchView mSearchView;
    private LinearLayout selectedLinearLayout = null;
    ConstraintLayout menuconstraint;

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
        // Retrieve the data passed from HomeFragment
        Bundle args = getArguments();
        if (args != null) {
            data1 = args.getString("data", "bunontop");
        }
        item_view(data1);
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

        card_bunontop = view.findViewById(R.id.bunontop_card);
        card_streetwok = view.findViewById(R.id.streetwok_card);
        card_bowlexpress = view.findViewById(R.id.bowlexpress_card);
        card_vadapavexpress=view.findViewById(R.id.mvadapavexpress_card);
        card_kfc= view.findViewById(R.id.mkfc_card);
        container_search=view.findViewById(R.id.search_container);
        mSearchView = view.findViewById(R.id.menu_searchView);
        menuconstraint=view.findViewById(R.id.constraintLayout_rest);
        recyclerview = view.findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        item_view_search();
        mAdapter2=new search_menu_adapter(getContext(),mUploads2);
        mFilteredAdapter=new search_menu_adapter(getContext(),mFilteredUploads);
        recyclerview.setAdapter(mAdapter);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                mSearchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
                menuconstraint.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                // Set the adapter to show all menu items when the search view is clicked
                recyclerview.setAdapter(mAdapter2);
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Restore the default menu and make the constraint visible again
                menuconstraint.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                recyclerview.setAdapter(mAdapter);
                return false;
            }
        });

        handleCardViewSelection(data1);
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
                handleCardViewSelection(name);
            }
        });

        card_streetwok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "streetwok";
                item_view(name);
                handleCardViewSelection(name);
            }
        });

        card_bowlexpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "bowlexpress";
                item_view(name);
                handleCardViewSelection(name);
            }
        });

        card_vadapavexpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "vadapavexpress";
                item_view(name);
                handleCardViewSelection(name);
            }
        });

        card_kfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "KFC";
                item_view(name);
                handleCardViewSelection(name);
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
    private void handleCardViewSelection(String folderName) {
        LinearLayout clickedLinearLayout = null;

        // Determine the clicked card view based on the folder name
        // Determine the clicked linear layout based on the folder name
        if ("bunontop".equals(folderName)) {
            clickedLinearLayout = card_bunontop.findViewById(R.id.linear_bunontop_card);
        } else if ("streetwok".equals(folderName)) {
            clickedLinearLayout = card_streetwok.findViewById(R.id.linear_streetwok_card);
        } else if ("bowlexpress".equals(folderName)) {
            clickedLinearLayout = card_bowlexpress.findViewById(R.id.linear_bowlexpress_card);
        } else if ("KFC".equals(folderName)) {
            clickedLinearLayout = card_bowlexpress.findViewById(R.id.linear_kfc_card);
        } else if ("vadapavexpress".equals(folderName)) {
            clickedLinearLayout = card_bowlexpress.findViewById(R.id.linear_vadapavexpress_card);
        }

        if (clickedLinearLayout != null) {
            // If there is a selected linear layout, restore its background
            if (selectedLinearLayout != null) {
                selectedLinearLayout.setBackgroundResource(R.drawable.curved_shape); // Restore normal drawable
            }

            // Set the background drawable of the clicked linear layout
            clickedLinearLayout.setBackgroundResource(R.drawable.curved_shape_dark); // Darkened drawable
            selectedLinearLayout = clickedLinearLayout;
        }
        }

    public void openSearchActivity() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);

        // Apply slide-right animation
        getActivity().overridePendingTransition(R.anim.slide_right, R.anim.fade_out);

    }


    public void item_view(String rest_name) {
        String deault1="bunontop";
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
    public void item_view_search() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("menu");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            // Inside the ValueEventListener in HomeFragment
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads2.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Get the download URL from Firebase Storage and set it in the Upload object
                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                    // Retrieve the price from Firebase and set it in the Upload object
                    Double priceValue = postSnapshot.child("price").getValue(Double.class);
                    if (priceValue != null) {
                        upload.setPrice(priceValue);
                    }
                   mUploads2.add(upload);
                }
              mAdapter2.notifyDataSetChanged();
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
    private void filterItems(String query) {
        mFilteredUploads.clear();

        if (query.isEmpty()) {
            mFilteredUploads.addAll(mUploads2); // Show all items when query is empty
        } else {
            for (Upload upload : mUploads2) {
                if (upload.getName().toLowerCase().contains(query.toLowerCase())) {
                    mFilteredUploads.add(upload);
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
        item_view(data1);
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

}

