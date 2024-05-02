package com.svvaap.superdrop2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class RestFilter_Dailoge extends DialogFragment{
    private RecyclerView recyclerView;

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog window background to transparent
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    public RestFilter_Dailoge(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rest_filter_dailoge, container, false);

        recyclerView=view.findViewById(R.id.rest_recyclerview);


    return view;
    }
    }