package com.madness.restaurant.daily;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyFragment extends Fragment {

    private DailyListener listener;

    // fake content for list
    private ArrayList<String> dishNames = new ArrayList<>();
    private ArrayList<Integer> dishPics =  new ArrayList<Integer>();
    private ArrayList<String> quantities = new ArrayList<>();
    private ArrayList<String> types = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public interface DailyListener {
        public void addDailyOffer();
    }

    public static DailyFragment newInstance(int columnCount) {
        // Required empty public constructor
        DailyFragment fragment = new DailyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DailyListener) {
            listener = (DailyListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListner");
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = (FloatingActionButton) getActivity().findViewById(R.id.dailyFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addDailyOffer();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the fragment layout
        View rootView =  inflater.inflate(R.layout.fragment_dailyoffers, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));
        // initialize the fake content
        initElements();

        RecyclerView recyclerView = rootView.findViewById(R.id.dishes);
        DailyOfferRecyclerViewAdapter adapter = new DailyOfferRecyclerViewAdapter(getContext(),dishNames,dishPics,quantities,types,prices);
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




    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void initElements(){

        dishNames.add("Pizza Margherita");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(R.drawable.dish_icon);
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");


    }

}