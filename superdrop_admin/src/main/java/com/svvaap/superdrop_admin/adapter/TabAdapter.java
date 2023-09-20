package com.svvaap.superdrop_admin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.svvaap.superdrop_admin.NewOrders;
import com.svvaap.superdrop_admin.OldOrders;

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
