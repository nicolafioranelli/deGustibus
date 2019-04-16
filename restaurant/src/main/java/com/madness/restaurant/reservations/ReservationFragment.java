package com.madness.restaurant.reservations;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
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
    List<ReservationClass> reservationList = new ArrayList<>();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    private ReservationsDataAdapter mAdapter;
    private SwipeController swipeController;
    private int position=0;

    private void setReservationsDataAdapter() {

        mAdapter = new ReservationsDataAdapter(reservationList);
    }
    private ReservationListener listener;

    public void addOnReservation() {
        ReservationClass reservationClass = new ReservationClass();
        reservationClass.setName(pref.getString("reservationName", getResources().getString(R.string.reservation_customerName)));
        reservationClass.setSeats(pref.getString("reservationSeats", "0"));
        reservationClass.setIdentifier(position);
        reservationClass.setDate_time(pref.getString("reservationTime", "13:00"));
        reservationClass.setDesc(pref.getString("reservationDesc", null));
        reservationClass.setOrderDishes(pref.getString("reservationOrderedDishes", getResources().getString(R.string.reservation_dishesOrdered)));
        mAdapter.add(position,reservationClass);
    }

    public interface ReservationListener {
        public void addReservation();
    }
    public ReservationFragment() {
        // Required empty public constructor
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = (FloatingActionButton) getActivity().findViewById(R.id.resFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("reservationName", getResources().getString(R.string.reservation_customerName));
                editor.putString("reservationSeats", "0");
                editor.putInt("reservaationIdentifier", position+1);
                editor.putString("reservationTime", "13:00");
                editor.putString("reservationDesc", null);
                editor.putString("reservationOrderedDishes", getResources().getString(R.string.reservation_dishesOrdered));
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
                Toast.makeText(getActivity(), "position = ", Toast.LENGTH_SHORT).show();

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
    }/*
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
