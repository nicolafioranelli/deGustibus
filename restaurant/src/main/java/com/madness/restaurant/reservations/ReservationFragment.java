package com.madness.restaurant.reservations;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment {


    // fake content for list
    ArrayList<ReservationClass> reservationList = new ArrayList<>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private ReservationsDataAdapter mAdapter;
    private SwipeController swipeController;
    private int replaced=0;
    private int addedposition=0;
    private  boolean added=true;

    private void setReservationsDataAdapter() {
        mAdapter = new ReservationsDataAdapter(reservationList);
    }
    private ReservationListener listener;

    public interface ReservationListener {
        public void addReservation();
    }
    public ReservationFragment() {
        // Required empty public constructor
        fakeContent();
    }

    private void fakeContent() {
        ReservationClass reservation = new ReservationClass(
                "Nicola Sabino",1,"3","18/04/2019",
                "21:00","Risotto ai frutti di mare","");
        this.reservationList.add(reservation);

        ReservationClass reservation2 = new ReservationClass(
                "Luca Rossi",2,"4","18/04/2019",
                "21:00","Pasta al pomodoro","");
        this.reservationList.add(reservation2);

        ReservationClass reservation3 = new ReservationClass(
                "Jacopo Iezzi",3,"2","18/04/2019",
                "21:00","Pizza margherita","");
        this.reservationList.add(reservation3);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ReservationListener) {
            listener = (ReservationListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListner");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        setReservationsDataAdapter();
    }

    public void addOnReservation() {
        ReservationClass reservationClass = new ReservationClass(
                pref.getString("reservationName", getResources().getString(R.string.reservation_customerNameEdit)),
                pref.getInt("reservationIdentifier",0),
                pref.getString("reservationSeats", "0"),
                pref.getString("reservationDay", "01/01/2019"),
                pref.getString("reservationTime", "13:00"),
                pref.getString("reservationOrderedDishes", getResources().getString(R.string.reservation_dishesOrderededit)),
                pref.getString("reservationDesc", getResources().getString(R.string.reservation_reservationDescedit))
                );

        if(added)
            mAdapter.add(addedposition,reservationClass);
        if(!added){
            mAdapter.add(replaced,reservationClass);
            mAdapter.remove(replaced-1);
            mAdapter.notifyItemRemoved(replaced-1);
            mAdapter.notifyItemRangeChanged(replaced-1, mAdapter.getItemCount());
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = (FloatingActionButton) getActivity().findViewById(R.id.resFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                added=true;
                addedposition=mAdapter.getItemCount();
                editor.putString("reservationName", getResources().getString(R.string.reservation_customerNameEdit));
                editor.putString("reservationSeats", "0");
                editor.putInt("reservationIdentifier",mAdapter.getItemCount());
                editor.putString("reservationTime", "13:00");
                editor.putString("reservationDay", "01/01/2019");
                editor.putString("reservationDesc", getResources().getString(R.string.reservation_reservationDescedit));
                editor.putString("reservationOrderedDishes", getResources().getString(R.string.reservation_dishesOrderededit));
                editor.apply();
                listener.addReservation();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the fragment layout
        View rootView =  inflater.inflate(R.layout.fragment_reservations, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Reservations));
        // initialize the fake content
        //initElements();

        recyclerView = rootView.findViewById(R.id.recyclerView);
        mAdapter = new ReservationsDataAdapter(reservationList);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // add a separator
        //DividerItemDecoration decoration = new DividerItemDecoration(getContext(), manager.getOrientation());
        //recyclerView.addItemDecoration(decoration);

        // set swipe controller
        swipeController=new SwipeController((new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                added=false;
                replaced=position+1;
                editor.putString("reservationName", mAdapter.getReservation(position).getName());
                editor.putString("reservationSeats", mAdapter.getReservation(position).getSeats());
                editor.putInt("reservationIdentifier", mAdapter.getReservation(position).getIdentifier());
                editor.putString("reservationTime", mAdapter.getReservation(position).getTime());
                editor.putString("reservationDay", mAdapter.getReservation(position).getDate());
                editor.putString("reservationDesc", mAdapter.getReservation(position).getDesc());
                editor.putString("reservationOrderedDishes", mAdapter.getReservation(position).getOrderDishes());
                editor.apply();
                listener.addReservation();

                Log.d("MAD", "onLeftClicked: left");
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                mAdapter.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                Log.d("MAD", "onLeftClicked: right");
                super.onRightClicked(position);
            }
        }));
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Reservations", new ArrayList<>(mAdapter.getList()));
    }
    /* Method to load shared preferences */
    private void loadSharedPrefs(){

    }/*
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            reservationList = bundle.getParcelableArrayList("Reservations");
        }
    }*/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            reservationList = savedInstanceState.getParcelableArrayList("Reservations");
            setReservationsDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }/*
    @Override
    public void onResume() {
        recyclerView = getView().findViewById(R.id.recyclerView);
        LinearLayoutManager manager  = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        mAdapter =new ReservationsDataAdapter(reservationList);
        recyclerView.setAdapter(mAdapter);
        super.onResume();
    }*/

    /*
    private void setupRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }*/
/*
    private void initElements(){

        names.add("Mario Rossi");
        identifiers.add(String.valueOf(1));
        seats.add(String.valueOf(4));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Luca Verdi");
        identifiers.add(String.valueOf(2));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("21:00");

        names.add("Giuseppe Neri");
        identifiers.add(String.valueOf(3));
        seats.add(String.valueOf(6));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Raffaella Rossi");
        identifiers.add(String.valueOf(4));
        seats.add(String.valueOf(1));
        dates.add("8/04/2019");
        time.add("19:00");

        names.add("Bruno Verdi");
        identifiers.add(String.valueOf(5));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("20:30");

        names.add("Mario Rossi");
        identifiers.add(String.valueOf(6));
        seats.add(String.valueOf(4));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Luca Verdi");
        identifiers.add(String.valueOf(7));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("21:00");

        names.add("Giuseppe Neri");
        identifiers.add(String.valueOf(8));
        seats.add(String.valueOf(6));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Raffaella Rossi");
        identifiers.add(String.valueOf(9));
        seats.add(String.valueOf(1));
        dates.add("8/04/2019");
        time.add("19:00");

        names.add("Bruno Verdi");
        identifiers.add(String.valueOf(10));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("20:30");

        names.add("Mario Rossi");
        identifiers.add(String.valueOf(11));
        seats.add(String.valueOf(4));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Luca Verdi");
        identifiers.add(String.valueOf(12));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("21:00");

        names.add("Giuseppe Neri");
        identifiers.add(String.valueOf(13));
        seats.add(String.valueOf(6));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Raffaella Rossi");
        identifiers.add(String.valueOf(14));
        seats.add(String.valueOf(1));
        dates.add("8/04/2019");
        time.add("19:00");

        names.add("Bruno Verdi");
        identifiers.add(String.valueOf(15));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("20:30");

        names.add("Mario Rossi");
        identifiers.add(String.valueOf(16));
        seats.add(String.valueOf(4));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Luca Verdi");
        identifiers.add(String.valueOf(17));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("21:00");

        names.add("Giuseppe Neri");
        identifiers.add(String.valueOf(18));
        seats.add(String.valueOf(6));
        dates.add("8/04/2019");
        time.add("20:00");

        names.add("Raffaella Rossi");
        identifiers.add(String.valueOf(19));
        seats.add(String.valueOf(1));
        dates.add("8/04/2019");
        time.add("19:00");

        names.add("Bruno Verdi");
        identifiers.add(String.valueOf(20));
        seats.add(String.valueOf(2));
        dates.add("8/04/2019");
        time.add("20:30");

    }
*/
}