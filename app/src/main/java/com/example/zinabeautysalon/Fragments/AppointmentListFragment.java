package com.example.zinabeautysalon.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zinabeautysalon.Adapters.AppointmentAdapter;
import com.example.zinabeautysalon.MainActivity;
import com.example.zinabeautysalon.R;


public class AppointmentListFragment extends Fragment {
    private RecyclerView list_RV_appointements;
    private AppointmentAdapter appointmentAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appointement_list_fragment, container, false);
        list_RV_appointements = view.findViewById(R.id.list_RV_appointements);

        if (MainActivity.getIsManagerConnected())
            appointmentAdapter = new AppointmentAdapter(getContext(), ManagerFragment.getInstance().getAppointments());
        else
            appointmentAdapter = new AppointmentAdapter(getContext(), MainFragment.getInstance().getAppointments());

        list_RV_appointements.setHasFixedSize(true);
        list_RV_appointements.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list_RV_appointements.setAdapter(appointmentAdapter);
        return view;
    }


}