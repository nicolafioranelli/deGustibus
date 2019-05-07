package com.madness.degustibus.daily;

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

import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.R;

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
    private DailyDataAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int replaced = 0;
    private int addedposition = 0;
    private boolean added = true;
    private int mColumnCount = 1;

    public DailyFragment() {
        // Required empty public constructor();
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
    private void setDailyDataAdapter() {mAdapter = new DailyDataAdapter(getContext(),dailyList);}

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
        //setDailyDataAdapter();
    }

    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dailyoffer, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily));

        recyclerView = rootView.findViewById(R.id.dishes);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        databaseReference.child("Offers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getAllOffers(dataSnapshot);
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
        });
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            setDailyDataAdapter();
            recyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /* Here is defined the interface for the HomeActivity in order to manage the click */
    public interface DailyListener {
        void addOfferOnReservations();
    }
    private void getAllOffers(DataSnapshot dataSnapshot) {
        DailyClass dailyClass = dataSnapshot.getValue(DailyClass.class);
        if(dailyClass.getRestaurant().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
        dailyList.add(new DailyClass(dailyClass.getDish(), dailyClass.getType(), dailyClass.getAvail(), dailyClass.getPrice(), dailyClass.getPic()));
        mAdapter = new DailyDataAdapter(getContext(), dailyList);
        recyclerView.setAdapter(mAdapter);
        }
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
}