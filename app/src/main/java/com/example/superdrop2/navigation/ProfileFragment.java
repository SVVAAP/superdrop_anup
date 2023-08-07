package com.example.superdrop2.navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.superdrop2.Admin_Activity;
import com.example.superdrop2.R;
import com.example.superdrop2.upload.rest_add_Activity;
import com.example.superdrop2.upload.Offer_Add_Activity;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

  Button admin,logout;

    public ProfileFragment() {
        // Required empty public constructor
    } @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        admin = view.findViewById(R.id.adminbt);
        logout=view.findViewById(R.id.logout);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Admin_Activity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().signOut();
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }


}