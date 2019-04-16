package com.madness.restaurant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class DayPickerFragment extends DialogFragment  {
    int year;
    int month;
    int dayOfMonth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        return  new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(),year,month,dayOfMonth);
        // Create a new instance of TimePickerDialog and return it
    }
}
