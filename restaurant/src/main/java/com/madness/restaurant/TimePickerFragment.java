package com.madness.restaurant;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    Calendar c=Calendar.getInstance();
    int hour=c.get(Calendar.HOUR_OF_DAY);
    int minute=c.get(Calendar.MINUTE);
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        return  new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(),hour, minute,android.text.format.DateFormat.is24HourFormat(getActivity()));
        // Create a new instance of TimePickerDialog and return it
    }
}
