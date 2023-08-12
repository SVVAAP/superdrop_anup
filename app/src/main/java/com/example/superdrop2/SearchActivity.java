package com.example.superdrop2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.superdrop2.adapter.delet_Adapter;
import com.example.superdrop2.adapter.rest_Adapter;
import com.example.superdrop2.upload.Upload;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private Button searchButton;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<String> searchResults;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FirebaseApp.initializeApp(this);
        searchView = findViewById(R.id.searchView);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerview);

        db = FirebaseFirestore.getInstance();
        searchResults = new ArrayList<>();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchView.getQuery().toString();
                if (!query.isEmpty()) {
                    searchItems(query);
                } else {
                    Toast.makeText(SearchActivity.this, "Enter an item name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchItems(String query) {
        // Clear previous results
        searchResults.clear();

        // Replace with your collection paths for each restaurant
        String[] restaurantPaths = {"bunotop", "streetwok", "bowlexpress"};

        for (String restaurantPath : restaurantPaths) {
            db.collection(restaurantPath)
                    .whereEqualTo("name", query)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                searchResults.add(document.getString("name"));
                            }
                            displaySearchResults();
                        } else {
                            Toast.makeText(SearchActivity.this, "Error searching items", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displaySearchResults() {
        // Initialize and set up your RecyclerView with the searchResults using the SearchResultsAdapter
        // Example: SearchResultsAdapter adapter = new SearchResultsAdapter(searchResults);
        // recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}

