package com.example.superdrop2.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.superdrop2.R;
import com.example.superdrop2.adapter.ImageAdapter;
import com.example.superdrop2.adapter.MyMenuAdapter;
import com.example.superdrop2.adapter.SliderAdapter;
import com.example.superdrop2.adapter.postview;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.methods.ezyMenuItem;
import com.example.superdrop2.upload.Upload;
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

public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private rest_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<Upload> mUploads;
    SliderView sliderView;
    private List<String> imageURLs = new ArrayList<>();

    //animatiopn in homr page
    private View rootView;
    private Animation fadeInAnimation;
    private Animation slideUpAnimation;
    private RecyclerView offerRecyclerView;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private  SwipeRefreshLayout swipeRefreshLayout;
private ImageView no_internet;

//    private CardView c1,c2,c3;
//    int[] images ={R.drawable.one,
//            R.drawable.one,
//            R.drawable.one,
//            R.drawable.one,
//            R.drawable.one};

    RecyclerView postmodle;
    List<postview> postviewlist;
    private ImageAdapter imageAdapter;
    private DatabaseReference mdatabaseref;


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
       // no_internet=view.findViewById(R.id.hno_internet_layout);
        offerRecyclerView=view.findViewById(R.id.offer_recyclerview);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            no_internet.setVisibility(View.VISIBLE);
        }

      // swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Implement the logic to refresh your data here
//                refreshData();
//            }
//        });

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


        // Initialize RecyclerView for offers
        return view;
    }

    private void openMenuFragment(String data) {
        NavActivity navActivity = (NavActivity) requireActivity();
        navActivity.loadMenuFragment(data);
    }
    private void fetchImageURLs() {
        // Get a reference to the "uploads" folder in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Offers");

        // Fetch the images from Firebase Storage
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                imageURLs.clear(); // Clear the existing URLs before adding new ones

                // Iterate through the list of items (images) and add their URLs to the list
                List<StorageReference> items = listResult.getItems();
                List<Task<Uri>> downloadUrlTasks = new ArrayList<>();

                for (StorageReference item : items) {
                    downloadUrlTasks.add(item.getDownloadUrl());
                }

                // Wait for all downloadUrlTasks to complete
                Tasks.whenAllComplete(downloadUrlTasks)
                        .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                            @Override
                            public void onSuccess(List<Task<?>> tasks) {
                                // Extract the URLs from the completed tasks
                                for (Task<Uri> task : downloadUrlTasks) {
                                    if (task.isSuccessful()) {
                                        Uri uri = task.getResult();
                                        imageURLs.add(uri.toString());
//                                        mAdapter=new rest_Adapter(imageURLs)
//                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                }
                                SliderAdapter slideadapter = new SliderAdapter(imageURLs);
                                sliderView.setSliderAdapter(slideadapter);
                                mProgressCircle.setVisibility(View.INVISIBLE);
                                sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                                sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                                sliderView.startAutoCycle();
                                ImageAdapter imageAdapter = new ImageAdapter(getActivity(), imageURLs);
                                offerRecyclerView.setAdapter(imageAdapter);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors that may occur while fetching URLs
                                Toast.makeText(getActivity(), "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that may occur while listing items in Firebase Storage
                Toast.makeText(getActivity(), "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

}





