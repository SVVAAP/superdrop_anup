package com.svvaap.superdrop2;

import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.svvaap.superdrop2.navigation.MenuFragment;

import java.util.Objects;


public class CatogeryFilter_Dailoge extends DialogFragment {


    @Override
    public void onStart() {
        super.onStart();
        // Set dialog window background to transparent
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    private Button apply,cancle;
    private RadioButton selectedRadioButton;
    private Chip selectedChip;
    private String selectedChipText="", selectedRadioText="";
    public CatogeryFilter_Dailoge() {

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catogery_filter_dailoge, container, false);

        apply=view.findViewById(R.id.bt_apply);
        cancle=view.findViewById(R.id.bt_cancle);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        ChipGroup chipGroup=view.findViewById(R.id.chipGroup);

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                selectedChip = view.findViewById(checkedId);
                if (selectedChip != null) {
                    selectedChipText = selectedChip.getText().toString();
                }
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if both chip and radio button are selected

                // Check if either chip or radio button is selected
                if (selectedChipText != null || selectedRadioText != null) {
                    // Pass the selected values to MenuFragment
                    ((MenuFragment) getParentFragment()).applyFilters(selectedChipText, selectedRadioText);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Please select either a chip or a radio button", Toast.LENGTH_SHORT).show();
                }
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected radio button
                selectedRadioButton = view.findViewById(checkedId);
                if (selectedRadioButton != null) {
                    selectedRadioText = selectedRadioButton.getText().toString();
                }
            }
        });
        return view;
    }
}