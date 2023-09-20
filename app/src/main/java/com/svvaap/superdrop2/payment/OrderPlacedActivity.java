package com.svvaap.superdrop2.payment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.superdrop2.R;
import com.svvaap.superdrop2.customers_Activity;

public class OrderPlacedActivity extends AppCompatActivity {
    Button track;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        track = findViewById(R.id.track_button);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderPlacedActivity.this, customers_Activity.class);
                startActivity(intent);
            }
        });

        // Load the GIF image
        ImageView gifImageView = findViewById(R.id.imageView8);
        Glide.with(this)
                .asGif()
                .load(R.drawable.ic_order_placed)
                .into(gifImageView);

        // Initialize and start the MediaPlayer to play audio
        mediaPlayer = MediaPlayer.create(this, R.raw.placeorder_ring); // Replace with your audio file
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
