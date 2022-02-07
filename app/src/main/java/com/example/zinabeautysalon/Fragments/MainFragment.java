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


public class MainFragment extends Fragment {
    private MaterialTextView mainFragment_TXT_greeting;
    private MaterialTextView mainFragment_TXT_appoinments;
    private OnFragmentInteractionListener listener;
    private static MainFragment instance = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointmentsRef;
    private FrameLayout mainFragment_FRM_appointments;
    ArrayList<Appointment> appointments = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mainFragment_TXT_greeting = view.findViewById(R.id.mainFragment_TXT_greeting);
        mainFragment_FRM_appointments = view.findViewById(R.id.mainFragment_FRM_appointments);
        mainFragment_TXT_appoinments = view.findViewById(R.id.mainFragment_TXT_appoinments);
        mainFragment_TXT_greeting.setText(MainActivity.greetingToUser());
        firebaseDatabase = FirebaseDatabase.getInstance();
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        instance = this;
        inflateAppointments();
        return view;

    }

    private void sortAppointments() {
        Comparator<Appointment> comparator = Comparator.comparing(appointment -> appointment.getDate());
        comparator = comparator.thenComparing(Comparator.comparing(appointment -> appointment.getTime()));
        Collections.sort(appointments,comparator);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new LogoFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFragment_FRM_logo, childFragment).commit();
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

    public static MainFragment getInstance() {
        return instance;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }


    private void inflateAppointments() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Appointment appointment = child.getValue(Appointment.class);
                        if(((MainActivity) getActivity()).getGoogleUser()!=null){
                            if (appointment.getAccountEmail().equals(((MainActivity) getActivity()).getGoogleUser().getEmail()))
                                appointments.add(appointment);
                        }
                        else{
                                if (appointment.getAccountEmail().equals(((MainActivity) getActivity()).getUser().getEmail()))
                                    appointments.add(appointment);
                        }


                    } catch (Exception e) {
                    }

                }
                if (appointments.size() > 0) {
                    sortAppointments();
                    Fragment childFragment = new AppointmentListFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainFragment_FRM_appointments, childFragment).commit();
                    mainFragment_TXT_appoinments.setText("התורים שלי:");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}