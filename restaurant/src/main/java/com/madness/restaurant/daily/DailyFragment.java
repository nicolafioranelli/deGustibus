package com.madness.restaurant.daily;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

import java.util.ArrayList;

/**
 * The DailyFragment class is in charge of presenting a ListItem View where will be displayed
 * the different dishes that the Restaurateur will prepare. This is obtained by means of the
 * DailyDataAdapter. The saving functionality is not implemented since will be enlarged with the
 * usage of Firebase.
 */
public class DailyFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    ArrayList<DailyClass> dailyList = new ArrayList<>();
    private DailyListener listener;
    private RecyclerView recyclerView;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private DailyDataAdapter mAdapter;
    private SwipeController swipeController;
    private int replaced = 0;
    private int addedposition = 0;
    private boolean added = true;
    private int mColumnCount = 1;

    public DailyFragment() {
        fakeContent();
    }

    /* Here is set the content to be shown, this method will be removed from the following lab */
    private void fakeContent() {
        DailyClass daily = new DailyClass("Pizza", "Pizza margherita senza mozzarella", "10", "20", null);
        this.dailyList.add(daily);

        DailyClass daily1 = new DailyClass("Spaghetti", "Pasta e sugo al ragù", "10", "15", null);
        this.dailyList.add(daily1);

        DailyClass daily2 = new DailyClass("Seppie e piselli", "Seppie del mar Adriatico e piselli bio", "10", "20", null);
        this.dailyList.add(daily2);
    }

    /* Here is set the Adapter */
    private void setDailyDataAdapter() {
        mAdapter = new DailyDataAdapter(dailyList);
    }

    /* The onAttach method registers the listener */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DailyListener) {
            listener = (DailyListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListner");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        setDailyDataAdapter();
    }

    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dailyoffers, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));

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

        // set swipe controller
        swipeController = new SwipeController((new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                added = false;
                replaced = position + 1;
                editor.putString("dish", mAdapter.getDailyClass(position).getDish());
                editor.putString("descDish", mAdapter.getDailyClass(position).getType());
                editor.putString("avail", mAdapter.getDailyClass(position).getAvail());
                editor.putString("price", mAdapter.getDailyClass(position).getPrice());
                editor.putString("photoDish", mAdapter.getDailyClass(position).getPic());
                editor.apply();
                listener.addDailyOffer();
                //Log.d("MAD", "onLeftClicked: left");
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                mAdapter.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                //Log.d("MAD", "onLeftClicked: right");
                super.onRightClicked(position);
            }
        }), this.getContext());

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        return rootView;
    }

    /* Here is set the click listener on the floating button */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton resFb = getActivity().findViewById(R.id.dailyFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                added = true;
                addedposition = mAdapter.getItemCount();
                editor.putString("dish", getResources().getString(R.string.frDaily_defName));
                editor.putString("descDish", getResources().getString(R.string.frDaily_defDesc));
                editor.putString("avail", String.valueOf(0));
                editor.putString("price", String.valueOf(0.00));
                editor.putString("photoDish", null);
                editor.apply();
                listener.addDailyOffer();
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Dailies", new ArrayList<>(mAdapter.getList()));
    }

    public void addOnDaily() {
        DailyClass dailyClass = new DailyClass(
                pref.getString("dish", getResources().getString(R.string.reservation_customerNameEdit)),
                pref.getString("descDish", "0"),
                pref.getString("avail", "01/01/2019"),
                pref.getString("price", "13:00"),
                pref.getString("photoDish", getResources().getString(R.string.reservation_dishesOrderededit))
        );
        if (added)
            mAdapter.add(addedposition, dailyClass);
        if (!added) {
            mAdapter.add(replaced, dailyClass);
            mAdapter.remove(replaced - 1);
            mAdapter.notifyItemRemoved(replaced - 1);
            mAdapter.notifyItemRangeChanged(replaced - 1, mAdapter.getItemCount());
        }
    }

    /* Here is defined the interface for the HomeActivity in order to manage the click */
    public interface DailyListener {
        void addDailyOffer();
    }
}