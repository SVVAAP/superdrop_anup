package com.svvaap.superdrop2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebView;

public class Abtdev_Activity extends AppCompatActivity {

    private LinearLayout anupLayout;
    private LinearLayout srujanLayout;
    private ImageView imageView3;
    private TextView textView3;
    private TextView svvaapDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abtdev);

        //loading website
        WebView webView = findViewById(R.id.svvaap_web);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript if required
        webView.loadUrl("https://svvaap.github.io/svvaap/");

        // Initialize your views
        anupLayout = findViewById(R.id.anup_layout);
        srujanLayout = findViewById(R.id.srujan_layout);
        imageView3 = findViewById(R.id.svvaap);
        textView3 = findViewById(R.id.textView3);
        svvaapDetails = findViewById(R.id.svvaap_details);

        // Load animations
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Animate views
        anupLayout.startAnimation(fadeInAnimation);
        srujanLayout.startAnimation(fadeInAnimation);
        imageView3.startAnimation(slideUpAnimation);
        textView3.startAnimation(slideUpAnimation);
        svvaapDetails.startAnimation(slideUpAnimation);
    }
}









