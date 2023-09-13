package com.example.superdrop_admin.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.superdrop_admin.NewOrders;
import com.example.superdrop_admin.OldOrders;

import java.util.ArrayList;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position){
           case 0:
               return new OldOrders();
           case 1:
               return new NewOrders();
           default:
               return new OldOrders();
       }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
