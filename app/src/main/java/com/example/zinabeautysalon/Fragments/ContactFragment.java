package com.example.zinabeautysalon.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zinabeautysalon.R;
import com.google.android.material.textview.MaterialTextView;


public class ContactFragment extends Fragment {
    private MaterialTextView contactFragment_TXT_info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        contactFragment_TXT_info = view.findViewById(R.id.contactFragment_TXT_info);
        setInfoText();
        return view;
    }

    private void setInfoText() {
        contactFragment_TXT_info.setText("כתובת : " + "שד' הציונות 1, קניון לוד\n" + "\n" + "שעות הפעילות:\n" + "א' - ה' : 10:00 - 20:00\n" + "ו' : 10:00 - 14:00\n" + "\n" + "טלפון : 0548039599");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new MapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.contactFragment_FRM_map, childFragment).commit();
    }


}