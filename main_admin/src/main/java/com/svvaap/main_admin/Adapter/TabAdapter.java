package com.svvaap.main_admin.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.svvaap.main_admin.NewFragment;
import com.svvaap.main_admin.OldFragment;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position){
           case 0:
               return new NewFragment();
           case 1:
           return new OldFragment();
           default:
               return new NewFragment();
       }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
