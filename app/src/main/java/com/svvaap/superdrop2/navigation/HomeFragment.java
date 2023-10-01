package com.svvaap.superdrop2.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.svvaap.superdrop2.AboutRestaurentActivity;
import com.svvaap.superdrop2.BottomSheet;
import com.svvaap.superdrop2.OfferActivity;
import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.adapter.ImageAdapter;
import com.svvaap.superdrop2.adapter.SliderAdapter;
import com.svvaap.superdrop2.adapter.postview;
import com.svvaap.superdrop2.adapter.rest_Adapter;
import com.svvaap.superdrop2.upload.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class  HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Upload> mUploads;
    private SliderView sliderView;
    private List<String> imageURLs = new ArrayList<>();
    private View rootView;
    private Animation fadeInAnimation;
    private Animation slideUpAnimation;
    private RecyclerView offerRecyclerView;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private  SwipeRefreshLayout swipeRefreshLayout;
    private ImageView no_internet,about;
    List<postview> postviewlist;
    private ImageAdapter imageAdapter;
    private DatabaseReference mdatabaseref, mDatabaseRef;


    public HomeFragment() {
        // Required empty public constructor
    }
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rootView=view;
 mProgressCircle = view.findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();
        postviewlist= new ArrayList<>();
        sliderView = view.findViewById(R.id.slider_view);
        no_internet=view.findViewById(R.id.hno_internet_layout);
        offerRecyclerView=view.findViewById(R.id.offer_recyclerview);
        ConstraintLayout gifLayout = view.findViewById(R.id.offers); // Replace with your actual ID
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            no_internet.setVisibility(View.VISIBLE);
        }

        // Set OnClickListener for SliderView
        sliderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle slider view click, open the Offer activity here
                openOfferActivity();
            }
        });
        // Initialize 'about' ImageView
        about = view.findViewById(R.id.more);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle 'about' ImageView click, open the AboutRestaurentActivity here
                openAboutRestaurentActivity();
            }
        });

        // Set OnClickListener for ConstraintLayout containing gifImageView
        gifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle GIF layout click, open the Offer activity here
                openOfferActivity();
            }
        });

       swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Implement the logic to refresh your data here
                refreshData();
            }
        });

        CardView cardBunontop = view.findViewById(R.id.bowlexpress_card);
        cardBunontop.setOnClickListener(v -> {
            String bunontopData = "bowlexpress";
            openMenuFragment(bunontopData);
        });

        CardView cardStreetwok = view.findViewById(R.id.streetwok_card);
        cardStreetwok.setOnClickListener(v -> {
            String streetwokData = "streetwok";
            openMenuFragment(streetwokData);
        });

        CardView cardBowlexpress = view.findViewById(R.id.bunontop_card);
        cardBowlexpress.setOnClickListener(v -> {
            String bowlexpressData = "bunontop";
            openMenuFragment(bowlexpressData);
        });

        CardView cardVadaPavexpress = view.findViewById(R.id.vadpavexpress_card);
        cardVadaPavexpress.setOnClickListener(v -> {
            String vadapavData = "vadapavexpress";
            openMenuFragment(vadapavData);
        });

        CardView cardKFC = view.findViewById(R.id.kfc_card);
        cardKFC.setOnClickListener(v -> {
            String kfcData = "KFC";
            openMenuFragment(kfcData);
        });

        // Initialize animations
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Apply animations
        rootView.startAnimation(fadeInAnimation);
        offerRecyclerView = view.findViewById(R.id.offer_recyclerview);
        offerRecyclerView.startAnimation(slideUpAnimation);

        // Load the GIF image
        ImageView gifImageView = view.findViewById(R.id.gifImageView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.offergif)
                .into(gifImageView);

        // Initialize slider view
        sliderView = view.findViewById(R.id.slider_view);
        fetchImageURLs();
        item_view();
        imageAdapter = new ImageAdapter(getActivity(), mUploads);
        offerRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });

        // Initialize RecyclerView for offers
        return view;
    }

    private void openMenuFragment(String data) {
        NavActivity navActivity = (NavActivity) requireActivity();
        navActivity.loadMenuFragment(data);
    }
    private void fetchImageURLs() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Offers");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            // Inside the ValueEventListener in HomeFragment
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageURLs.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    imageURLs.add(postSnapshot.child("imageUrl").getValue(String.class));
                }
                SliderAdapter slideadapter = new SliderAdapter(imageURLs);
                sliderView.setSliderAdapter(slideadapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
                sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                sliderView.startAutoCycle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void refreshData() {
        if (getView() == null) {
            return;
        }
        // Implement your data refresh logic here
        fetchImageURLs();


        // For example, you can re-fetch your data from Firebas
        if (!isNetworkAvailable()) {
            // No internet connection, display a toast message
            no_internet.setVisibility(View.VISIBLE);
        } else {
           no_internet.setVisibility(View.GONE);
            // After data is refreshed, stop the refresh animation
        }
        // After data is refreshed, stop the refresh animation
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000); // Delayed for 2 seconds to simulate data loading
    }
    public void openOfferActivity() {
        // Implement the logic to open your Offer activity here
        // For example:
        Intent offerIntent = new Intent(getActivity(), OfferActivity.class);
        startActivity(offerIntent);
    }

    public void openAboutRestaurentActivity() {

        Intent intent = new Intent(getActivity(), AboutRestaurentActivity.class);
        startActivity(intent);
    }
    public void item_view() {

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Offer_item");
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
                imageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
    private void showBottomSheetForItem(Upload item) {
        BottomSheet bottomSheetFragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("itemId", item.getItemId()); // Pass the itemId to the BottomSheet
        args.putString("name", item.getName());
        args.putString("imageUrl", item.getImageUrl());
        args.putDouble("price", Double.parseDouble(item.getDiscountPrice()));
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }

}





