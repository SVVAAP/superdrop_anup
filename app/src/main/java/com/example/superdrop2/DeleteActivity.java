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
    private RecyclerView recyclerview1,recyclerView2,recyclerView3;
    private delet_Adapter mAdapter;
    private ProgressBar mProgressCircle;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads,mUploads1,mUploads2,mUploads3;
    private String bunontop,streetwok,bowlexpress;
    private Button delet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        delet=findViewById(R.id.delet_menu);

        bunontop="bunontop";
        streetwok="streetwok";
        bowlexpress="bowlexpress";

        recyclerview1=findViewById(R.id.recyclerView_bunontop);
        recyclerview1.setHasFixedSize(true);
        recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2=findViewById(R.id.recyclerView_streetwok);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView3=findViewById(R.id.recyclerView_bowlexpress);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));

        mUploads1=item_view(bunontop);
        mAdapter = new delet_Adapter(this, mUploads1,1);
        recyclerview1.setAdapter(mAdapter);

        mUploads2=item_view(streetwok);
        mAdapter = new delet_Adapter(this, mUploads2,2);
        recyclerView2.setAdapter(mAdapter);

        mUploads3=item_view(bowlexpress);
        mAdapter = new delet_Adapter(this, mUploads3,3);
        recyclerView3.setAdapter(mAdapter);

        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedItems();
            }
        });
    }


    public List<Upload> item_view(String rest_name) {
        List<Upload> uploads = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(rest_name);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeleteActivity.this, "Error.... :(", Toast.LENGTH_SHORT).show();
            }
        });

        return uploads;
    }

    private void deleteSelectedItems() {
        List<Integer> selectedItems = mAdapter.getSelectedItems();
        int recyclerViewType = mAdapter.getRecyclerViewType(); // Get the recyclerViewType from the adapter

        DatabaseReference databaseReference = null; // Initialize database reference

        if (recyclerViewType == 1) {
            databaseReference = FirebaseDatabase.getInstance().getReference(bunontop);
        } else if (recyclerViewType == 2) {
            databaseReference = FirebaseDatabase.getInstance().getReference(streetwok);
        } else if (recyclerViewType == 3) {
            databaseReference = FirebaseDatabase.getInstance().getReference(bowlexpress);
        } else {
            return; // Handle an invalid recyclerViewType value here
        }

        for (int position : selectedItems) {
            Upload upload;
            if (recyclerViewType == 1) {
                upload = mUploads1.get(position);
            } else if (recyclerViewType == 2) {
                upload = mUploads2.get(position);
            } else if (recyclerViewType == 3) {
                upload = mUploads3.get(position);
            } else {
                return; // Handle an invalid recyclerViewType value here
            }

            if (upload != null) {
                DatabaseReference itemRef = databaseReference.child(upload.getItemId());
                itemRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Item deleted successfully
                        Toast.makeText(DeleteActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete item
                        Toast.makeText(DeleteActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Remove the selected items from the local list
        for (int position : selectedItems) {
            if (recyclerViewType == 1) {
                mUploads1.remove(position);
            } else if (recyclerViewType == 2) {
                mUploads2.remove(position);
            } else if (recyclerViewType == 3) {
                mUploads3.remove(position);
            } else {
                // Handle an invalid recyclerViewType value here if needed
            }
        }

        // Clear the selection and update the adapter
        mAdapter.getSelectedItems().clear();
        mAdapter.notifyDataSetChanged();
    }
}