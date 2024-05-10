package com.svvaap.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svvaap.superdrop2.R;
import com.svvaap.superdrop2.adapter.delet_Adapter;
import com.svvaap.superdrop2.adapter.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    private String data1, data = "bunontop"; //default data
    private RecyclerView recyclerview;
    private delet_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private Button  button_search,delete;
    private CardView card_bunontop, card_streetwok, card_bowlexpress;
    private FrameLayout container_search;
    private Boolean isEditMode=false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        mUploads = new ArrayList<>();

        // Retrieve the data passed from HomeFragment
        delete = findViewById(R.id.delet_bt);
        card_bunontop = findViewById(R.id.bunontop_card);
        card_streetwok = findViewById(R.id.streetwok_card);
        card_bowlexpress =findViewById(R.id.bowlexpress_card);
        container_search=findViewById(R.id.search_container);

        recyclerview = findViewById(R.id.fooditems_rv);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(DeleteActivity.this));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        SearchFragment searchFragment = new SearchFragment();
//        ft.replace(R.id.search_container, searchFragment); // Use replace instead of add
        ft.addToBackStack(null); // Add to back stack to allow navigation back
        ft.commit();
item_view(data);
        card_bunontop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "bunontop";
                data=name;
                item_view(name);
            }
        });

        card_streetwok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "streetwok";
                data=name;
                item_view(name);
            }
        });

        card_bowlexpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "bowlexpress";
                data=name;
                item_view(name);
            }
        });
//        button_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isEditMode) {
//                    show(true);
//                    button_search.setText("Search");
//                    ViewGroup.LayoutParams params = button_search.getLayoutParams();
//                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                    button_search.setLayoutParams(params);
//                    // Align the button to the center
//                    ConstraintSet constraintSet = new ConstraintSet();
//                    constraintSet.clone((ConstraintLayout) view.getParent());
//                    constraintSet.centerHorizontally(R.id.button2, ConstraintSet.PARENT_ID);
//                    constraintSet.applyTo((ConstraintLayout) view.getParent());
//                    button_search.animate()
//                            .translationXBy(0) // Shift to right side
//                            .setDuration(300) // Animation duration in milliseconds
//                            .start();
//                    imageView.setVisibility(View.VISIBLE);
//                    isEditMode = false;
//                } else {
//                    show(false);
//                    isEditMode = true;
//                    ViewGroup.LayoutParams params = button_search.getLayoutParams();
//                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                    button_search.setLayoutParams(params);
//                    button_search.setText("X");
//                    ConstraintSet constraintSet = new ConstraintSet();
//                    constraintSet.clone((ConstraintLayout) view.getParent());
//                    constraintSet.connect(R.id.button2, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
//                    constraintSet.clear(R.id.button2, ConstraintSet.START);
//                    constraintSet.applyTo((ConstraintLayout) view.getParent());
//                    DeleteActivity.this.overridePendingTransition(R.anim.slide_right, R.anim.fade_out);
//                    imageView.setVisibility(View.GONE);
//                }
//
//            }
//        });

        mAdapter = new delet_Adapter(DeleteActivity.this, mUploads);
        recyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected items to delete
                List<Upload> selectedItems = mAdapter.getSelectedItems();
                // Get the current folder name
               // String currentFolder = getCurrentFolderName();

                // Get reference to the Firebase database for "menu" and "bunontop" nodes
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference("menu");
                DatabaseReference currentFolderRef = FirebaseDatabase.getInstance().getReference(data);

                // Loop through selected items and delete them from both "menu" and "bunontop"
                for (Upload selectedItem : selectedItems) {
                    String itemId = selectedItem.getItemId(); // Assuming you have this property in Upload class
                    if (itemId != null) {
                        menuRef.child(itemId).removeValue();
                        currentFolderRef.child(itemId).removeValue();
                    }
                }

                // Notify user about successful deletion
                Toast.makeText(DeleteActivity.this, "Selected items deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private String getCurrentFolderName() {
        if (data.equals("bunontop")) {
            return "bunontop";
        } else if (data.equals("streetwok")) {
            return "streetwok";
        } else {
            return "bowlexpress";
        }
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
        Intent intent = new Intent(DeleteActivity.this, SearchActivity.class);
        startActivity(intent);

        // Apply slide-right animation
        DeleteActivity.this.overridePendingTransition(R.anim.slide_right, R.anim.fade_out);

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

    public void openMenuFragment(String itemName) {
        Bundle args = new Bundle();
        args.putString("itemName", itemName);

//        SearchFragment searchFragment = new SearchFragment();
//        searchFragment.setArguments(args);
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
}