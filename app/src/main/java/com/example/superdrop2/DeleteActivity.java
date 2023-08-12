package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.superdrop2.adapter.CartItem;
import com.example.superdrop2.adapter.delet_Adapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.upload.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    private RecyclerView recyclerview1, recyclerView2, recyclerView3;
    private delet_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads, mUploads1, mUploads2, mUploads3;
    private List<Upload> selectedUploads = new ArrayList<>(); // To store selected items
    private String bunontop, streetwok, bowlexpress;
    private Button delet;
    private List<String> mKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        delet = findViewById(R.id.delet_menu);

        bunontop = "bunontop";
        streetwok = "streetwok";
        bowlexpress = "bowlexpress";

        recyclerview1 = findViewById(R.id.recyclerView_bunontop);
        recyclerview1.setHasFixedSize(true);
        recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2 = findViewById(R.id.recyclerView_streetwok);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3 = findViewById(R.id.recyclerView_bowlexpress);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));

        mUploads=new ArrayList<>();
        mUploads1=new ArrayList<>();
        mUploads2=new ArrayList<>();
        mUploads3=new ArrayList<>();
        mKeys.clear();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(bunontop);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            // Inside the ValueEventListener in HomeFragment
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads1.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Get the download URL from Firebase Storage and set it in the Upload object
                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                    // Retrieve the price from Firebase and set it in the Upload object
                    Double priceValue = postSnapshot.child("price").getValue(Double.class);
                    if (priceValue != null) {
                        upload.setPrice(priceValue);
                    }
                    mUploads1.add(upload);
                    mKeys.add(postSnapshot.getKey());
                }
                mAdapter = new delet_Adapter(DeleteActivity.this, mUploads1);
                recyclerview1.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //streeet wok
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(streetwok);
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
                    mKeys.add(postSnapshot.getKey());
                }

                mAdapter = new delet_Adapter(DeleteActivity.this, mUploads2);
                recyclerView2.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //bowl express
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(bowlexpress);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            // Inside the ValueEventListener in HomeFragment
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads3.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Get the download URL from Firebase Storage and set it in the Upload object
                    upload.setImageUrl(postSnapshot.child("imageUrl").getValue(String.class));
                    // Retrieve the price from Firebase and set it in the Upload object
                    Double priceValue = postSnapshot.child("price").getValue(Double.class);
                    if (priceValue != null) {
                        upload.setPrice(priceValue);
                    }
                    mUploads3.add(upload);
                    mKeys.add(postSnapshot.getKey());
                }
                mAdapter = new delet_Adapter(DeleteActivity.this, mUploads3);
                recyclerView3.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedItems();
                Toast.makeText(DeleteActivity.this, "trying to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteSelectedItems() {
        // Remove selected items from Firebase and update the RecyclerView
        for (Upload upload : selectedUploads) {
            String key = mKeys.get(mUploads1.indexOf(upload)); // Adjust index for correct list
            DatabaseReference itemRef = mDatabaseRef.child(key);

            // Perform the delete operation with OnSuccessListener
            itemRef.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Item successfully deleted from Firebase
                            Toast.makeText(DeleteActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure, show error message
                            Toast.makeText(DeleteActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        selectedUploads.clear(); // Clear the list of selected items
        mAdapter.notifyDataSetChanged(); // Update the RecyclerView
    }
}