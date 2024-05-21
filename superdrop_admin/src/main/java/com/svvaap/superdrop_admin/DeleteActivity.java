package com.svvaap.superdrop_admin;

import static com.google.common.io.Files.getFileExtension;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superdrop_admin.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.svvaap.superdrop_admin.adapter.ImageAdapter;
import com.svvaap.superdrop_admin.adapter.delet_Adapter;
import com.svvaap.superdrop_admin.adapter.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    private RecyclerView recyclerview;
    private delet_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private ImageAdapter imageAdapter;
    private List<Upload> mUploads;
    private Button  button_search,delete;
    private FrameLayout container_search;
    private Boolean isEditMode=false;
    ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        mUploads = new ArrayList<>();

        // Retrieve the data passed from HomeFragment
        delete = findViewById(R.id.delet_bt);
        container_search=findViewById(R.id.search_container);
        imageView =findViewById(R.id.imageView7);

        recyclerview = findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(DeleteActivity.this));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SearchFragment searchFragment = new SearchFragment();
        ft.replace(R.id.search_container, searchFragment); // Use replace instead of add
        ft.addToBackStack(null); // Add to back stack to allow navigation back
        ft.commit();

        mAdapter = new delet_Adapter(DeleteActivity.this, mUploads);
        recyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        item_view();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected items to delete
                List<Upload> selectedItems = mAdapter.getSelectedItems();
                // Get the current folder name
               // String currentFolder = getCurrentFolderName();

                // Get reference to the Firebase database for "menu" and "bunontop" nodes
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                DatabaseReference currentFolderRef = FirebaseDatabase.getInstance().getReference("menu");
                StorageReference storageReference= FirebaseStorage.getInstance().getReference("menu");

                // Loop through selected items and delete them from both "menu" and "bunontop"
                for (Upload selectedItem : selectedItems) {
                    String itemId = selectedItem.getItemId(); // Assuming you have this property in Upload class
                    String imageuri=selectedItem.getImageUrl();
                    if (itemId != null) {
                        menuRef.child(itemId).removeValue();
                        currentFolderRef.child(itemId).removeValue();
                        StorageReference fileRef = storageReference.child(itemId); // Replace ".jpg" with the actual file extension you use
                        fileRef.delete();

                    }
                }

                // Notify user about successful deletion
                Toast.makeText(DeleteActivity.this, "Selected items deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void show(Boolean editMode){
        if (container_search != null) { // Add this null check
            container_search.setVisibility(editMode ? View.GONE : View.VISIBLE);
            recyclerview.setVisibility(editMode ? View.VISIBLE : View.GONE);

        }
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
                    mAdapter.clearSelectedItems();
                }
                mAdapter.notifyDataSetChanged();
//                mAdapter = new rest_Adapter(getActivity(), mUploads);
//                recyclerview.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}