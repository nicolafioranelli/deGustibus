package com.madness.restaurant.reservations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;
import com.madness.restaurant.picker.DayPickerFragment;
import com.madness.restaurant.picker.TimePickerFragment;

/**
 * NewReservation Fragment class
 */
public class NewReservationFragment extends Fragment {

    LinearLayout lunchday;
    LinearLayout lunchtime;
    /* Views */
    private EditText name;
    private EditText portions;
    private EditText desc;
    private EditText orderDishes;
    private TextView day;
    private TextView time;
    private String id;
    private DatabaseReference databaseReference;
    private FirebaseUser user;


    public NewReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("reservations");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_reservation, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Reservations));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = getActivity().findViewById(R.id.et_edit_customerName);
        desc = getActivity().findViewById(R.id.et_edit_reservationDesc);
        portions = getActivity().findViewById(R.id.et_edit_customerSeats);
        orderDishes = getActivity().findViewById(R.id.et_edit_dishesOrdered);
        day = getActivity().findViewById(R.id.et_edit_lunchday);
        time = getActivity().findViewById(R.id.et_edit_lunchtime);

        Bundle bundle = getArguments();
        id = bundle.getString("id");
        if (id.equals("null")) {
            name.setText(getResources().getString(R.string.reservation_customerNameEdit));
            desc.setText(getResources().getString(R.string.reservation_reservationDescedit));
            portions.setText("0");
            orderDishes.setText(getResources().getString(R.string.reservation_dishesOrderededit));
            time.setText("13:00");
            day.setText("01/01/2019");
        } else {
            loadFromFirebase(bundle.getString("id"), view);
        }
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml) */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            /* Handle save option and go back */
            Toast.makeText(getContext(), getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
            storeOnFirebase();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate("RESERVATION", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDayAndTime() {
        lunchday = getActivity().findViewById(R.id.lunchDayLinearLayout);
        lunchday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DayPickerFragment();
                datePicker.show(getFragmentManager(), "date picker");
            }
        });
        lunchtime = getActivity().findViewById(R.id.lunchTimeLinearLayout);
        lunchtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });
    }

    public void setHourAndMinute(int hour, int minute) {
        time.setText(String.format("%02d:%02d", hour, minute));
    }

    public void setDate(int year, int month, int day) {
        this.day.setText(String.format("%2d/%2d/%4d", day, month, year));
    }

    @Override
    public void onResume() {
        super.onResume();
        getDayAndTime();
    }

    private void loadFromFirebase(String id, final View view) {
        Query query = databaseReference.child(user.getUid()).orderByKey().equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ReservationClass d = snapshot.getValue(ReservationClass.class);
                        name.setText(d.getFullname());
                        orderDishes.setText(d.getDish());
                        portions.setText(d.getPortions());
                        String[] datetime = d.getDatetime().split("\t");
                        time.setText(datetime[1]);
                        day.setText(datetime[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MAD", "onCancelled: ", databaseError.toException());
            }
        });
    }

    private void storeOnFirebase() {
        if (id.equals("null")) {  // -- NEW ELEMENT --
            final DatabaseReference newItem = databaseReference.child(user.getUid()).push();
            final String newItemKey = newItem.getKey();

            // create a new daily class container
            ReservationClass reservation = new ReservationClass(
                    name.getText().toString(),
                    String.valueOf(1),
                    orderDishes.getText().toString(),
                    portions.getText().toString(),
                    day.getText().toString().concat("\t").concat(time.getText().toString()));

            newItem.setValue(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("MAD", "Success!");
                }
            });
        } else {
            final DatabaseReference updateItem = databaseReference.child(user.getUid()).child(id);

            // create a new daily class container
            ReservationClass reservation = new ReservationClass(
                    name.getText().toString(),
                    String.valueOf(1),
                    orderDishes.getText().toString(),
                    portions.getText().toString(),
                    day.getText().toString().concat("\t").concat(time.getText().toString()));

            updateItem.setValue(reservation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("MAD", "Success!");
                }
            });
        }
    }
}


