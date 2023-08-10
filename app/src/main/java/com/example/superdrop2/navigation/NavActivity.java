package com.example.superdrop2.navigation;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.superdrop2.Abtdev_Activity;
import com.example.superdrop2.Cart_Activity;
import com.example.superdrop2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

        public class NavActivity extends AppCompatActivity {

            BottomNavigationView bnview;
            Toolbar toolbar;

            ImageView btn_cart,btn_logo;
            View view;
            @Override
            protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_cart= findViewById(R.id.cart_img);
        btn_logo=findViewById(R.id.logo_img);
        bnview =findViewById(R.id.nav_container);

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
        bnview.setSelectedItemId(R.id.nav_home);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavActivity.this, Cart_Activity.class);
                startActivity(intent);
            }
        });
        btn_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavActivity.this, Abtdev_Activity.class);
                startActivity(intent);
            }
        });
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
}
