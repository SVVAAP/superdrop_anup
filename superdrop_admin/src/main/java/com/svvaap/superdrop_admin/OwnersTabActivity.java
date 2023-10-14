package com.svvaap.superdrop_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.ComponentName;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(overlayIntent);
        }

        // Check if "Draw over other apps" permission is granted
        if (!Settings.canDrawOverlays(OwnersTabActivity.this)) {
            showPermissionRequestDialog();
        }
       // if (!isNotificationAccessGranted()) {
        //    requestNotificationAccessPermission();
     //   }
        // ...

        tabLayout =findViewById(R.id.tab_layout);
        viewPager2=findViewById(R.id.view_pager);
        tabAdapter=new TabAdapter(this);
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
    // Method to check if notification access is granted
//    private boolean isNotificationAccessGranted() {
//     //   ComponentName cn = new ComponentName(this, MyNotification.class);
//
//        // Get the package manager
//        PackageManager pm = getPackageManager();
//
//        // Check if the service is enabled
//        //int state = pm.getComponentEnabledSetting(cn);
//
//        //return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
//    }

    // Method to request notification access permission
    private void requestNotificationAccessPermission() {
        // Redirect the user to the notification access settings
        Toast.makeText(this, "Please turn On the Notification in System Seetings", Toast.LENGTH_SHORT).show();
    }

}