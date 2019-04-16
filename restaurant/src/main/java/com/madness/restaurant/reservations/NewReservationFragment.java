package com.madness.restaurant.reservations;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.madness.restaurant.BuildConfig;
import com.madness.restaurant.DayPickerFragment;
import com.madness.restaurant.R;
import com.madness.restaurant.TimePickerFragment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewReservationFragment extends Fragment {


    public NewReservationFragment() {
        // Required empty public constructor
    }


    /* Views */
    private EditText name;
    private EditText seats;
    private EditText desc;
    private EditText orderDishes;

    private TextView day;
    private TextView time;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    LinearLayout lunchday;
    LinearLayout lunchtime;
    Button submit;

    private NewReservationListener listener;

    public interface NewReservationListener {
        public void onSubmit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof NewReservationListener) {
            listener = (NewReservationListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement NewReservationListener");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);/*
        setContentView(R.layout.activity_add_new_daily_offer);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("deGustibus");
        toolbar.setSubtitle("Restaurant");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleColor));
        setSupportActionBar(toolbar);*/
        setHasOptionsMenu(true);


        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_new_reservation, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Reservations));
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = getActivity().findViewById(R.id.et_edit_customerName);
        desc = getActivity().findViewById(R.id.et_edit_reservationDesc);
        seats = getActivity().findViewById(R.id.et_edit_customerSeats);
        orderDishes = getActivity().findViewById(R.id.et_edit_dishesOrdered);
        day = getActivity().findViewById(R.id.et_edit_lunchday);
        time = getActivity().findViewById(R.id.et_edit_lunchtime);
        submit = getActivity().findViewById(R.id.button);
        if(savedInstanceState != null){
            loadBundle(savedInstanceState);
        }else{
            loadSharedPrefs();
        }
        getDayAndTime();
        getSubmit();
    }

    private void getSubmit() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("reservationName", name.getText().toString());
                editor.putString("reservationDesc", desc.getText().toString());
                editor.putString("reservationSeats",seats.getText().toString());
                editor.putString("reservationOrderedDishes",orderDishes.getText().toString());
                editor.putString("reservationTime", time.getText().toString());
                editor.putString("reservationDay", day.getText().toString());
                editor.putString("reservationDay", day.getText().toString());
                editor.apply();

                /* Handle save option and go back */
                Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                listener.onSubmit();

                //FragmentManager fragmentManager = getFragmentManager();
                //fragmentManager.popBackStackImmediate("RESERVATION", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }


    public void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        super.onSaveInstanceState(outState);

        outState.putString("reservationName", name.getText().toString());
        outState.putString("reservationDesc", desc.getText().toString());
        outState.putString("reservationSeats",seats.getText().toString());
        outState.putString("reservationOrderedDishes",orderDishes.getText().toString());
        outState.putString("reservationTime",time.getText().toString());
        outState.putString("reservationDay", day.getText().toString());
    }

    private void loadSharedPrefs(){
        name.setText(pref.getString("reservationName", getResources().getString(R.string.reservation_customerName)));
        desc.setText(pref.getString("reservationDesc", null));
        seats.setText(pref.getString("reservationSeats", "0"));
        orderDishes.setText(pref.getString("reservationOrderedDishes", getResources().getString(R.string.reservation_dishesOrdered)));
        time.setText(pref.getString("reservationTime", "13:00"));
        day.setText(pref.getString("reservationDay","01/01/2019"));
    }

    private void loadBundle(Bundle bundle){
        name.setText(bundle.getString("reservationName"));
        desc.setText(bundle.getString("reservationDesc"));
        seats.setText(bundle.getString("reservationSeats"));
        orderDishes.setText(bundle.getString("reservationOrderedDishes"));
        time.setText(bundle.getString("reservationTime"));
        day.setText(bundle.getString("reservationDay"));

    }
    public void getDayAndTime() {
        lunchday =getActivity().findViewById(R.id.lunchDayLinearLayout);
        lunchday.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                DialogFragment datePicker= new DayPickerFragment();
                datePicker.show(getFragmentManager(), "date picker");
            }
        });
        lunchtime =getActivity().findViewById(R.id.lunchTimeLinearLayout);
        lunchtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });
    }
    public void setHourAndMinute(int hour, int minute) {
        time.setText(String.format("%02d:%02d", hour, minute));
    }
    public  void  setDate(int year, int month, int day){
        this.day.setText(String.format("%4d/%2d/%2d", year,month,day));
    }
}
