package com.svvaap.superdrop2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.widget.TextView;

public class AboutRestaurentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_restaurent);

//        WebView webView = findViewById(R.id.streetwok_web);
//        // Enable JavaScript if required
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("https://svvaap.github.io/streetwok");

        TextView phoneTextView = findViewById(R.id.call);
        TextView addressTextView = findViewById(R.id.address);

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneDialer();
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    public void openMap() {
        WebView webView = findViewById(R.id.streetwok_web);
        // Enable JavaScript if required
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://maps.app.goo.gl/FVFa9u2X8XaZVNhv7");

    }

    public void openPhoneDialer() {
        String phoneNumber = "7304305584";
        Uri phoneUri = Uri.parse("tel:" + phoneNumber);
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            // Handle the case where there's no dialer app installed
            // You can display a message or take an alternative action.
        }
    }
}
