package com.svvaap.superdrop2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.svvaap.superdrop2.R;

import android.webkit.WebView;


public class AboutRestaurentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_restaurent);

        WebView webView = findViewById(R.id.streetwok_web);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript if required
        webView.loadUrl("https://svvaap.github.io/streetwok/");
    }
}
