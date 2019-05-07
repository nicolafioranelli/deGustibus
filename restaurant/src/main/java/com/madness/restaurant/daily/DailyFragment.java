package com.madness.restaurant.daily;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

import java.util.ArrayList;
import java.util.List;

/**
 * The DailyFragment class is in charge of presenting a ListItem View where will be displayed
 * the different dishes that the Restaurateur will prepare. This is obtained by means of the
 * DailyDataAdapter. The saving functionality is not implemented since will be enlarged with the
 * usage of Firebase.
 */
public class DailyFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private List<DailyClass> dailyList;
    private DailyListener listener;
    private RecyclerView recyclerView;
    private DailyDataAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SwipeController swipeController;
    private int replaced = 0;
    private int addedposition = 0;
    private boolean added = true;
    private int mColumnCount = 1;
    private FirebaseRecyclerAdapter adapter;

    public DailyFragment() {
        // Required empty public constructor();
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
    }

    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_dailyoffers, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));

        dailyList = new ArrayList<DailyClass>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.dishes);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // TODO add progress bar, also in the layout
        //rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

       /* ChildEventListener listener = databaseReference.child("Offers").addChildEventListener(new ChildEventListener() {
            int counter = 0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                counter++;
                Log.d("MAD", counter + " onChildAdded:" + dataSnapshot.getKey() + " string " + s);
                getAllOffers(dataSnapshot);
                //TODO manade the progress bar
                // rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getAllOffers(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                removeOffer(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        Query query = databaseReference.child("Offers");

        FirebaseRecyclerOptions<DailyClass> options =
                new FirebaseRecyclerOptions.Builder<DailyClass>()
                        .setQuery(query, DailyClass.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<DailyClass, DailyHolder>(options) {


            @Override
            public DailyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.dailyoffer_listitem, parent, false);

                return new DailyHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DailyHolder holder, int position, @NonNull DailyClass model) {
                holder.dish.setText(model.getDish());
                holder.type.setText(model.getType());
                holder.avail.setText(model.getAvail());
                holder.price.setText(model.getPrice());
            }

        };

        recyclerView.setAdapter(adapter);

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

                // TODO search for a better way to pass data between fragments
                editor.putString("dish", dailyList.get(position).getDish());
                editor.putString("descDish", dailyList.get(position).getType());
                editor.putString("avail", dailyList.get(position).getAvail());
                editor.putString("price", dailyList.get(position).getPrice());
                editor.putString("photoDish", dailyList.get(position).getPic());
                editor.putString("dishIdentifier", dailyList.get(position).getIdentifier());
                editor.apply();
                listener.addDailyOffer();
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                Query removeQuery = databaseReference.child("Offers").orderByChild("identifier").equalTo(dailyList.get(position).getIdentifier());
                removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                            singleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                editor.putString("dish", getResources().getString(R.string.frDaily_defName));
                editor.putString("descDish", getResources().getString(R.string.frDaily_defDesc));
                editor.putString("avail", String.valueOf(0));
                editor.putString("price", String.valueOf(0.00));
                editor.putString("photoDish", null);
                editor.putString("dishIdentifier", null);
                editor.apply();
                listener.addDailyOffer();
            }
        });
    }


    /* Here is defined the interface for the HomeActivity in order to manage the click */
    public interface DailyListener {
        void addDailyOffer();
    }
    private void getAllOffers(DataSnapshot dataSnapshot) {
        DailyClass dailyClass = dataSnapshot.getValue(DailyClass.class);
        dailyList.add(new DailyClass(dailyClass.getDish(),
                dailyClass.getType(),dailyClass.getAvail(),
                dailyClass.getPrice(),dailyClass.getPic(),
                dailyClass.getRestaurant(),
                dailyClass.getIdentifier()));
        mAdapter = new DailyDataAdapter(getContext(),dailyList);
        recyclerView.setAdapter(mAdapter);

    }
    private void removeOffer(DataSnapshot dataSnapshot) {
        DailyClass dailyClass = dataSnapshot.getValue(DailyClass.class);
        for (int i = 0; i < dailyList.size(); i++) {
            if (dailyList.get(i).getIdentifier().equals(dailyClass.getIdentifier())) {
                dailyList.remove(i);
            }
        }
        mAdapter.notifyDataSetChanged();
        mAdapter = new DailyDataAdapter(getContext(), dailyList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}