package com.svvaap.superdrop2.navigation;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.svvaap.superdrop2.Abtdev_Activity;
import com.svvaap.superdrop2.Cart_Activity;
import com.svvaap.superdrop2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
public class NavActivity extends AppCompatActivity {

    BottomNavigationView bnview;
    Toolbar toolbar;
    int ItemCount = 0;
    ImageView btn_cart, btn_logo;
    View view;
    TextView badgeNumber;
    private static NavActivity instance;
    // Define a constant for the notification permission request.
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1;
    private final boolean isNotificationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!isNotificationAccessGranted()) {
            showNotificationPermissionDialog();
        }

        btn_cart = findViewById(R.id.cart_img);
        btn_logo = findViewById(R.id.logo_img);
        bnview = findViewById(R.id.nav_container);
        badgeNumber = findViewById(R.id.badge_number); // Replace with your TextView ID
        bnview.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    loadflag(new ProfileFragment(), false);
                } else if (item.getItemId() == R.id.nav_home) {
                    loadflag(new HomeFragment(), true);
                } else if (item.getItemId() == R.id.nav_menu) {
                    loadflag(new MenuFragment(), false);
                }
                return true;
            }

        });
        instance = this;
        bnview.setSelectedItemId(R.id.nav_home);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavActivity.this, Cart_Activity.class);
                startActivity(intent);
                // Apply slide-right animation
                NavActivity.this.overridePendingTransition(R.anim.slide_right, R.anim.fade_out);
            }
        });
        btn_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavActivity.this, Abtdev_Activity.class);
                startActivity(intent);
                // Apply slide-right animation
                NavActivity.this.overridePendingTransition(R.anim.slide_right, R.anim.fade_out);
            }
        });


        // Check if the notification permission is granted.
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted, request it.
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
//                // The user has previously denied the permission, show a rationale.
//                showPermissionRationale();
//            } else {
//                // The user hasn't been asked for the permission yet.
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, NOTIFICATION_PERMISSION_REQUEST);
//            }
//        }

//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    requestNotificationPermission();
//                }




    }


    public void loadflag(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(flag)
            ft.add(R.id.bncontainer,fragment);
        else
            ft.replace(R.id.bncontainer,fragment);
        ft.commit();


    }
            public static NavActivity getInstance() {
                return instance;
            }
    public void clear(){
                ItemCount=0;
                badgeNumber.setVisibility(View.INVISIBLE);
    }
            public void updateBadgeNumber(int newItemCount) {
                ItemCount=ItemCount+newItemCount;
                badgeNumber.setText(String.valueOf(ItemCount));
                badgeNumber.setVisibility(ItemCount > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        // Your activity code
        public void loadMenuFragment(String data) {
            MenuFragment menuFragment = new MenuFragment();
            Bundle bundle = new Bundle();
            bundle.putString("data", data);
            menuFragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.bncontainer, menuFragment);
            ft.addToBackStack(null); // Add the fragment to the back stack if needed
            ft.commit();
        }

    private void showNotificationPermissionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Notification Permission Required");
        builder.setMessage("To receive notifications, please turn on notification access for this app.");

        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open notification access settings
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the user's choice to cancel
            }
        });

        builder.create().show();
    }

    private boolean isNotificationAccessGranted() {
        // For Android Oreo and above, you need to check the notification channel's importance.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = notificationManager.getNotificationChannel("Channel1"); // Replace with your channel ID

        return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;

    }

//    Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS);
}
