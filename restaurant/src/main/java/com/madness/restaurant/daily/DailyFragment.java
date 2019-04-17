package com.madness.restaurant.daily;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
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
import android.widget.Toast;

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
    ArrayList<DailyClass> dailyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private DailyDataAdapter mAdapter;
    private SwipeController swipeController;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int replaced=0;
    private int addedposition=0;
    private  boolean added=true;
    private int mColumnCount = 1;
/*
    public static DailyFragment newInstance(int columnCount) {
        // Required empty public constructor
        DailyFragment fragment = new DailyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }*/
    public interface DailyListener {
        public void addDailyOffer();
    }
    private void setDailyDataAdapter() {

        mAdapter = new DailyDataAdapter(dailyList);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);/*
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        setDailyDataAdapter();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = (FloatingActionButton) getActivity().findViewById(R.id.dailyFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                added=true;
                addedposition=mAdapter.getItemCount();
                editor.putString("dish", getResources().getString(R.string.dish_name));
                editor.putString("descDish", getResources().getString(R.string.desc_dish));
                editor.putString("avail", String.valueOf(0));
                editor.putString("price", String.valueOf(0.00));
                editor.putString("photoDish", null);
                editor.apply();
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
        //initElements();

        recyclerView = rootView.findViewById(R.id.dishes);
        mAdapter = new DailyDataAdapter(dailyList);
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
                editor.putString("dish", mAdapter.getDailyClass(position).getDish());
                editor.putString("descDish", mAdapter.getDailyClass(position).getType());
                editor.putString("avail", mAdapter.getDailyClass(position).getAvail());
                editor.putString("price", mAdapter.getDailyClass(position).getPrice());
                editor.putString("photoDish", mAdapter.getDailyClass(position).getPic());
                editor.apply();
                listener.addDailyOffer();

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
        outState.putParcelableArrayList("Dailies", new ArrayList<>(mAdapter.getList()));
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dailyList = savedInstanceState.getParcelableArrayList("Dailies");
            setDailyDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }

    public void addOnDaily() {
        DailyClass dailyClass = new DailyClass(
                pref.getString("dish", getResources().getString(R.string.reservation_customerNameEdit)),
                pref.getString("descDish", "0"),
                pref.getString("avail", "01/01/2019"),
                pref.getString("price", "13:00"),
                pref.getString("photoDish", getResources().getString(R.string.reservation_dishesOrderededit))
        );
        if(added)
            mAdapter.add(addedposition,dailyClass);
        if(!added){
            mAdapter.add(replaced,dailyClass);
            mAdapter.remove(replaced-1);
            mAdapter.notifyItemRemoved(replaced-1);
            mAdapter.notifyItemRangeChanged(replaced-1, mAdapter.getItemCount());
        }
    }/*
    private void initElements(){

        dishNames.add("Pizza Margherita");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");

        dishNames.add("Pizza Margherita");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(4));
        types.add("primo");
        prices.add("3.50$");

        dishNames.add("Pasta Carbonara");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(3));
        types.add("primo");
        prices.add("2.50$");

        dishNames.add("Pezza Invinada");
        dishPics.add(getResources().getDrawable(R.drawable.dish_icon));
        quantities.add(String.valueOf(1));
        types.add("secondo");
        prices.add("3.50$");


    }*/

}