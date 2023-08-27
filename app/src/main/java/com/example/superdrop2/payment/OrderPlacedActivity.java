package com.example.superdrop2.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.superdrop2.R;
import com.example.superdrop2.customers_Activity;

public class OrderPlacedActivity extends AppCompatActivity {
Button track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        track=findViewById(R.id.track_button);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderPlacedActivity.this, customers_Activity.class);
                startActivity(intent);
            }
        });
    }
}
