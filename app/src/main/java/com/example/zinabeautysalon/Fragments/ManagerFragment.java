package com.example.zinabeautysalon.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.zinabeautysalon.MainActivity;
import com.example.zinabeautysalon.Models.Appointment;
import com.example.zinabeautysalon.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ManagerFragment extends Fragment {
    private MaterialTextView managerFragment_TXT_greeting;
    private MaterialTextView managerFragment_TXT_appoinments;
    private static ManagerFragment instance = null;
    private OnFragmentInteractionListener listener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointmentsRef;
    private FrameLayout managerFragment_FRM_appointments;
    ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_fragment, container, false);
        managerFragment_TXT_greeting = view.findViewById(R.id.managerFragment_TXT_greeting);
        managerFragment_FRM_appointments = view.findViewById(R.id.managerFragment_FRM_appointments);
        managerFragment_TXT_appoinments = view.findViewById(R.id.managerFragment_TXT_appoinments);
        managerFragment_TXT_greeting.setText(MainActivity.greetingToUser());
        firebaseDatabase = FirebaseDatabase.getInstance();
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        instance = this;
        inflateAppointments();
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void messageFromParentFragment(Uri uri);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    public static ManagerFragment getInstance() {
        return instance;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    private void sortAppointments() {
        Comparator<Appointment> comparator = Comparator.comparing(appointment -> appointment.getDate());
        comparator = comparator.thenComparing(Comparator.comparing(appointment -> appointment.getTime()));
        Collections.sort(appointments, comparator);
    }

    private void inflateAppointments() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Appointment appointment = child.getValue(Appointment.class);
                        appointments.add(appointment);
                    } catch (Exception e) {
                    }

                }
                if (appointments.size() > 0) {
                    sortAppointments();
                    Fragment childFragment = new AppointmentListFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.managerFragment_FRM_appointments, childFragment).commit();
                    managerFragment_TXT_appoinments.setText("תורים עתידיים:");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}