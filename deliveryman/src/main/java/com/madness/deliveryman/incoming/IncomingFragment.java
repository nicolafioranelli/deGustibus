package com.madness.deliveryman.incoming;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.R;
import com.madness.deliveryman.swipe.SwipeController;
import com.madness.deliveryman.swipe.SwipeControllerActions;

import java.util.ArrayList;
import java.util.List;

public class IncomingFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SwipeController swipeController;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;

    public IncomingFragment() {
        // Required empty public constructor();
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

        final View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Incoming));

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        recyclerView = rootView.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("orders");

        FirebaseRecyclerOptions<IncomingData> options =
                new FirebaseRecyclerOptions.Builder<IncomingData>()
                        .setQuery(query, IncomingData.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<IncomingData, IncomingHolder>(options) {

            @NonNull
            @Override
            public IncomingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.incoming_item, viewGroup, false);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                return new IncomingHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomingHolder holder, int position, @NonNull IncomingData model) {
                holder.restaurantName.setText(model.getRestName());
                holder.restaurantAddress.setText(model.getRestAddress());
                holder.customerName.setText(model.getCostumerName());
                holder.customerAddress.setText(model.getAddress());
                holder.customerPhone.setText(model.getCostumerPhone());
                holder.date.setText(model.getDate());
                holder.hour.setText(model.getHour());
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
            public void onRightClicked(int position) {
                databaseReference = db.getReference();
                Query removeQuery = databaseReference.child("orders")
                        .child(user.getUid())
                        .orderByChild("identifier")
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
