package com.madness.restaurant;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment {


    // fake content for list
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> identifiers = new ArrayList<>();
    private ArrayList<String> seats = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();


    private ReservationListener listener;

    public interface ReservationListener {
        public void setReservationBarTitle(String title);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listener.setReservationBarTitle( getResources().getString(R.string.menu_reservations));
        // inflate the fragment layout
        View rootView =  inflater.inflate(R.layout.fragment_reservation, container, false);
        // initialize the fake content
        initElements();

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(),names,identifiers,seats,dates,time);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);



        // add a separator
        //DividerItemDecoration decoration = new DividerItemDecoration(getContext(), manager.getOrientation());
        //recyclerView.addItemDecoration(decoration);


        // set swipe controller
        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                Log.d("MAD", "onLeftClicked: left");
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                Log.d("MAD", "onLeftClicked: right");
                super.onRightClicked(position);
            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        return rootView;

    }



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

}
