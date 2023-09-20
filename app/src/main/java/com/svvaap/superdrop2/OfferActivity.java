package com.svvaap.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.superdrop2.R;
import com.svvaap.superdrop2.adapter.ImageAdapter;
import com.svvaap.superdrop2.adapter.SliderAdapter;
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

public class OfferActivity extends AppCompatActivity {
private RecyclerView  offerRecyclerView;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private ImageAdapter imageAdapter;
    private ProgressBar mProgressCircle;
    private SliderView sliderView;
    private List<String> imageURLs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        mUploads = new ArrayList<>();
        offerRecyclerView=findViewById(R.id.offer_recyclerview2);
        offerRecyclerView.setHasFixedSize(true);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        item_view();
        imageAdapter = new ImageAdapter(this, mUploads);
        mProgressCircle = findViewById(R.id.oprogress_circle);
        sliderView = findViewById(R.id.oslider_view);
        fetchImageURLs();
        offerRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Upload item) {
                showBottomSheetForItem(item);
            }
        });


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
                Toast.makeText(OfferActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showBottomSheetForItem(Upload item) {
        BottomSheet bottomSheetFragment = new BottomSheet();
        Bundle args = new Bundle();
        args.putString("itemId", item.getItemId()); // Pass the itemId to the BottomSheet
        args.putString("name", item.getName());
        args.putString("imageUrl", item.getImageUrl());
        args.putDouble("price", Double.parseDouble(item.getDiscountPrice()));
        bottomSheetFragment.setArguments(args);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
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
                                Toast.makeText(OfferActivity.this, "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that may occur while listing items in Firebase Storage
                Toast.makeText(OfferActivity.this, "Failed to fetch image URLs", Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

}
