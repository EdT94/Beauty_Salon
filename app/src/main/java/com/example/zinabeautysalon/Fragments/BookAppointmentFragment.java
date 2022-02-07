package com.example.zinabeautysalon.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.zinabeautysalon.MainActivity;
import com.example.zinabeautysalon.Models.Appointment;
import com.example.zinabeautysalon.Models.MyDate;
import com.example.zinabeautysalon.Models.MyTime;
import com.example.zinabeautysalon.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;


public class BookAppointmentFragment extends Fragment {
    private TextInputEditText bookAppointmentFragment_EDT_dateInput;
    private AutoCompleteTextView bookAppointmentFragment_AUT_treatmentInput;
    private AutoCompleteTextView bookAppointmentFragment_AUT_hourInput;
    private MaterialTextView bookAppointmentFragment_TXT_validation;
    private MaterialButton bookAppointmentFragment_BUT_bookButton;
    private ArrayAdapter<String> arrayAdapterTreatments;
    private ArrayAdapter<MyTime> arrayAdapterHours;
    private Handler handler = new Handler();
    private String oldDateHint = "";
    private String oldTreatmentHint = "";
    private String oldHourHint = "";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointmentsRef;
    private ArrayList<MyTime> illegalTimes = new ArrayList<MyTime>();
    private ArrayList<String> treatments = new ArrayList<String>() {
        {
            add("מניקור");
            add("פדיקור");
            add("גבות");
            add("טיפול פנים");
            add("בניית ציפורניים");
            add("לק ג'ל");
            add("הרמת ריסים");
            add("תספורת גבר");
        }
    };

    private ArrayList<MyTime> hours = new ArrayList<MyTime>() {
        {
            add(new MyTime("10", "00"));
            add(new MyTime("10", "30"));
            add(new MyTime("11", "00"));
            add(new MyTime("11", "30"));
            add(new MyTime("12", "00"));
            add(new MyTime("12", "30"));
            add(new MyTime("13", "00"));
            add(new MyTime("13", "30"));
            add(new MyTime("14", "00"));
            add(new MyTime("14", "30"));
            add(new MyTime("15", "00"));
            add(new MyTime("15", "30"));
            add(new MyTime("16", "00"));
            add(new MyTime("16", "30"));
            add(new MyTime("17", "00"));
            add(new MyTime("17", "30"));
            add(new MyTime("18", "00"));
            add(new MyTime("18", "30"));
            add(new MyTime("19", "00"));
            add(new MyTime("19", "30"));
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_appointment_fragment, container, false);
        bookAppointmentFragment_EDT_dateInput = view.findViewById(R.id.bookAppointmentFragment_EDT_dateInput);
        bookAppointmentFragment_AUT_treatmentInput = view.findViewById(R.id.bookAppointmentFragment_AUT_treatmentInput);
        bookAppointmentFragment_BUT_bookButton = view.findViewById(R.id.bookAppointmentFragment_BUT_bookButton);
        bookAppointmentFragment_TXT_validation = view.findViewById(R.id.bookAppointmentFragment_TXT_validation);
        bookAppointmentFragment_AUT_hourInput = view.findViewById(R.id.bookAppointmentFragment_AUT_hourInput);
        bookAppointmentFragment_TXT_validation.setVisibility(View.INVISIBLE);
        oldDateHint = bookAppointmentFragment_EDT_dateInput.getHint().toString();
        oldTreatmentHint = bookAppointmentFragment_AUT_treatmentInput.getHint().toString();
        oldHourHint = bookAppointmentFragment_AUT_hourInput.getHint().toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        maintainInputs();
        return view;
    }


    public void maintainInputs() {
        bookAppointmentFragment_EDT_dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePicker();
            }
        });

        bookAppointmentFragment_AUT_hourInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookAppointmentFragment_EDT_dateInput.getText().toString().equals("")) {
                    showAlert("בחר תאריך");
                } else {
                    findHourstoShow();
                }
            }
        });


        bookAppointmentFragment_BUT_bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (bookAppointmentFragment_EDT_dateInput.getText().toString().equals("") ||
                            bookAppointmentFragment_AUT_treatmentInput.getText().toString().equals("") ||
                            bookAppointmentFragment_AUT_hourInput.getText().toString().equals("")
                    )
                        showAlert("מלא את כל פרטי התור");
                    else {
                        bookAppoinment();
                        reset();
                    }
                } catch (ParseException e) {
                }
            }
        });
        arrayAdapterTreatments = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, treatments);
        bookAppointmentFragment_AUT_treatmentInput.setAdapter(arrayAdapterTreatments);
    }

    private void findHourstoShow() {
        String dateFromUser = bookAppointmentFragment_EDT_dateInput.getText().toString();
        MyDate date = new MyDate();
        String[] arrOfDate = dateFromUser.split("/", -1);
        //arrOfDate[0] = day,arrayOfDate[1] = month, arrayOfDate[2] = year

        date.setDay(arrOfDate[0]).setMonth(arrOfDate[1]).setYear(arrOfDate[2]);

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Appointment appointment = child.getValue(Appointment.class);
                        if (appointment.getDate().compareTo(date) == 0) {
                            illegalTimes.add(appointment.getTime());
                        }
                    } catch (Exception e) {
                    }

                }
                hours.removeAll(illegalTimes);
                arrayAdapterHours = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, hours);
                bookAppointmentFragment_AUT_hourInput.setAdapter(arrayAdapterHours);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void reset() {
        Toast.makeText(getContext(), "התור הוזמן בהצלחה", Toast.LENGTH_SHORT).show();
        bookAppointmentFragment_EDT_dateInput.setText(oldDateHint);
        bookAppointmentFragment_AUT_treatmentInput.setText(oldTreatmentHint);
        bookAppointmentFragment_AUT_hourInput.setText(oldHourHint);
        Fragment fragment = new MainFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showAlert(String message) {
        bookAppointmentFragment_TXT_validation.setText(message);
        bookAppointmentFragment_TXT_validation.setVisibility(View.VISIBLE);
        bookAppointmentFragment_TXT_validation.startAnimation(shakeError());
        handler.postDelayed(new Runnable() {
            public void run() {
                bookAppointmentFragment_TXT_validation.setVisibility(View.INVISIBLE);
            }
        }, 800);


    }

    private void bookAppoinment() throws ParseException {
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        String dateFromUser = bookAppointmentFragment_EDT_dateInput.getText().toString();
        String timeFromUser = bookAppointmentFragment_AUT_hourInput.getText().toString();
        MyDate date = new MyDate();
        MyTime time = new MyTime();
        String[] arrOfDate = dateFromUser.split("/", -1);
        //arrOfDate[0] = day,arrayOfDate[1] = month, arrayOfDate[2] = year

        String[] arrOfTime = timeFromUser.split(":", -1);
        //arrOfTime[0] = hour, arrOfTime[1] = minutes

        date.setDay(arrOfDate[0]).setMonth(arrOfDate[1]).setYear(arrOfDate[2]);
        time.setHours(arrOfTime[0]).setMinutes(arrOfTime[1]);
        Appointment appointment = new Appointment();

        if (((MainActivity) getActivity()).getGoogleUser() != null)
            appointment.setDate(date).setTime(time).setAccount(((MainActivity) getActivity()).getGoogleUser().getEmail()).setTreatment(bookAppointmentFragment_AUT_treatmentInput.getText().toString()).setApproved(false);
        else
            appointment.setDate(date).setTime(time).setAccount(((MainActivity) getActivity()).getUser().getEmail()).setTreatment(bookAppointmentFragment_AUT_treatmentInput.getText().toString()).setApproved(false);

        appointmentsRef.push().setValue(appointment);
    }


    private void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "DatePicker");

    }


    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }
}