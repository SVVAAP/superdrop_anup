package com.svvaap.superdrop_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.superdrop_admin.R;
import com.svvaap.superdrop_admin.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class OwnersTabActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabAdapter tabAdapter;
    private static final int DRAW_OVER_OTHER_APPS_PERMISSION_REQUEST = 123; // Replace with your request code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_tab);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "Default"; // Use the same channel ID you are trying to access
            if (notificationManager.getNotificationChannel(channelId) == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH; // Adjust importance as needed
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Default Channel", importance);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // Check if "Draw over other apps" permission is granted
        if (!Settings.canDrawOverlays(OwnersTabActivity.this)) {
            showPermissionRequestDialog();
        }
        if (!isNotificationAccessGranted()) {
            showNotificationPermissionDialog();
        }
        // ...

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        tabAdapter = new TabAdapter(this);
        viewPager2.setAdapter(tabAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ImageView postImageView = findViewById(R.id.upload_bt);
        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Admin_Activity when ImageView is clicked
                Intent intent = new Intent(OwnersTabActivity.this, Admin_Activity.class);
                startActivity(intent);
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


    }

    private void showPermissionRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("To continue, please grant the 'Draw over other apps' permission.");

        // Add OK button to open permission settings
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestDrawOverOtherAppsPermission();
            }
        });

        builder.create().show();
    }

    private void requestDrawOverOtherAppsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, DRAW_OVER_OTHER_APPS_PERMISSION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DRAW_OVER_OTHER_APPS_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // "Draw over other apps" permission granted
            } else {
                // Permission not granted
                // You can handle this as needed
            }
        }
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            NotificationChannel channel = notificationManager.getNotificationChannel("Default"); // Replace with your channel ID

            return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;

    }


//    Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS);
}