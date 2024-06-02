package com.svvaap.main_admin;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svvaap.main_admin.Adapter.Feedback;
import com.svvaap.main_admin.Adapter.FeedbackAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppFeedbackActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private DatabaseReference databaseFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);

        recyclerView = findViewById(R.id.recyclerViewFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(feedbackAdapter);

        databaseFeedback = FirebaseDatabase.getInstance().getReference("feedback");

        databaseFeedback.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = postSnapshot.getValue(Feedback.class);
                    feedbackList.add(feedback);
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FeedbackListActivity", "Failed to read feedback", databaseError.toException());
            }
        });
    }
}