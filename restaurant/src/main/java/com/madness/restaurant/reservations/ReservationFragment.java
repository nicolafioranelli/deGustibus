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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;


/**
 * ReservationFragment class
 */
public class ReservationFragment extends Fragment {

    private ReservationListener listener;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private ValueEventListener emptyListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SwipeController swipeController;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;

    public interface ReservationListener {
        public void addReservation(String identifier);
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
            throw new ClassCastException(context.toString() + "must implement ReservationListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_reservations, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Reservations));

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("reservations").child(user.getUid());

        FirebaseRecyclerOptions<ReservationClass> options =
                new FirebaseRecyclerOptions.Builder<ReservationClass>()
                        .setQuery(query, ReservationClass.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ReservationClass, ReservationHolder>(options) {

            @NonNull
            @Override
            public ReservationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.reservation_listitem, viewGroup, false);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                return new ReservationHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReservationHolder holder, int position, @NonNull ReservationClass model) {
                holder.fullname.setText(model.getFullname());
                holder.identifier.setText(model.getIdentifier());
                holder.dish.setText(model.getDish());
                holder.portions.setText(model.getPortions());
                holder.datetime.setText(model.getDatetime());
            }

        };

        recyclerView.setAdapter(adapter);

        /* Listener to check if the recycler view is empty */
        emptyListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rootView.findViewById(R.id.emptyLayout).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                listener.addReservation(adapter.getRef(position).getKey());
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                databaseReference = db.getReference();
                Query removeQuery = databaseReference.child("reservations")
                        .child(user.getUid())
                        .orderByKey()
                        .equalTo(adapter.getRef(position).getKey());

                removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
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
        FloatingActionButton resFb = getActivity().findViewById(R.id.resFab);
        resFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addReservation("null");
            }
        });
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

    @Override
    public void onDetach() {
        super.onDetach();
        databaseReference.removeEventListener(emptyListener);
    }
}