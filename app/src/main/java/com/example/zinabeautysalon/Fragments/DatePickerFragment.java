package com.example.zinabeautysalon.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.zinabeautysalon.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private TextInputEditText bookAppointmentFragment_EDT_dateInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date_picker_fragment, container, false);
        bookAppointmentFragment_EDT_dateInput = (TextInputEditText) getActivity().findViewById(R.id.bookAppointmentFragment_EDT_dateInput);
        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Get yesterday's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerStyle, this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        return datePickerDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, day - 1);
        String dayOfWeek = simpledateformat.format(date);
        if (dayOfWeek.equals("יום שבת")) {
            Toast.makeText(getContext(), "לא ניתן להזמין תור בשבת", Toast.LENGTH_SHORT).show();
            return;
        }
        populateSetDate(year, month + 1, day);
    }

    public void populateSetDate(int yearFromUser, int monthFromUser, int dayFromUser) {
        bookAppointmentFragment_EDT_dateInput.setText(dayFromUser + "/" + monthFromUser + "/" + yearFromUser);
    }
}