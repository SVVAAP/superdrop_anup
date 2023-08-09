package com.example.superdrop2.navigation;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private MyMenuAdapter myMenuAdapter;

    //animatiopn in homr page
    private View rootView;
    private Animation fadeInAnimation;
    private Animation slideUpAnimation;
    private RecyclerView offerRecyclerView;



//    private CardView c1,c2,c3;
//    int[] images ={R.drawable.one,
//            R.drawable.one,
//            R.drawable.one,
//            R.drawable.one,
//            R.drawable.one};

    RecyclerView postmodle;
    ArrayList<postview> postviewlist;
    private ImageAdapter madapter;
    private DatabaseReference mdatabaseref;
    public HomeFragment() {
        // Required empty public constructor
    }
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Initialize the rootView
        rootView = view;

//                               ezy menu code here



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
            myMenuAdapter = new MyMenuAdapter(menuItems);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            mRecyclerView.setAdapter(myMenuAdapter);




//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle = view.findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        //slider view
        sliderView = view.findViewById(R.id.slider_view);

        fetchImageURLs();

//        mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            // Inside the ValueEventListener in HomeFragment
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mUploads.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Upload upload = postSnapshot.getValue(Upload.class);
//                    // Get the download URL from Firebase Storage and set it in the Upload object
//                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
//                    mUploads.add(upload);
//                }
//                mAdapter = new rest_Adapter(getActivity(), mUploads);
//                mRecyclerView.setAdapter(mAdapter);
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressCircle.setVisibility(View.INVISIBLE);
//            }
//        });


        CardView cardBunontop = view.findViewById(R.id.card_bowlexpress);
        cardBunontop.setOnClickListener(v -> {
            String bunontopData = "bowlexpress";
            openMenuFragment(bunontopData);
        });

        CardView cardStreetwok = view.findViewById(R.id.card_streetwok);
        cardStreetwok.setOnClickListener(v -> {
            String streetwokData = "streetwok";
            openMenuFragment(streetwokData);
        });

        CardView cardBowlexpress = view.findViewById(R.id.card_bunontop);
        cardBowlexpress.setOnClickListener(v -> {
            String bowlexpressData = "bunontop";
            openMenuFragment(bowlexpressData);
        });

        // Other click listeners and code
        postmodle = view.findViewById(R.id.offer_recyclerview);
        postviewlist = new ArrayList<>();
        postmodle.setHasFixedSize(true);
         postmodle.setLayoutManager(new LinearLayoutManager(getActivity()));


        postviewlist = new ArrayList<>();
        mdatabaseref= FirebaseDatabase.getInstance().getReference("Offers");
        mdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapShort : snapshot.getChildren()){
                    postview upload=postSnapShort.getValue(postview.class);
                    postviewlist.add(upload);
                }
                madapter=new ImageAdapter(getActivity(),postviewlist);
                postmodle.setAdapter(madapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // animation code here
        // Load animations
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Apply animations
        rootView.startAnimation(fadeInAnimation);
        offerRecyclerView = rootView.findViewById(R.id.offer_recyclerview);
        offerRecyclerView.startAnimation(slideUpAnimation);

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

}





