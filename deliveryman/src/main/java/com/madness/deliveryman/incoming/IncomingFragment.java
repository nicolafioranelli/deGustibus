package com.madness.deliveryman.incoming;

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
    private IncomingAdapter incomingAdapter;
    private DatabaseReference databaseReference;
    private List<IncomingData> incomings;
    private SwipeController swipeController;

    public IncomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);
        getActivity().setTitle(getString(R.string.title_Incoming));

        incomings = new ArrayList<IncomingData>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        databaseReference.child("orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getAllIncomings(dataSnapshot);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getAllIncomings(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                removeIncoming(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        swipeController=new SwipeController((new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                databaseReference = FirebaseDatabase.getInstance().getReference();
                Query removeQuery = databaseReference.child("orders").orderByChild("restaurateur").equalTo(incomings.get(position).getRestaurateur());
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
        }),this.getContext());

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

    private void getAllIncomings(DataSnapshot dataSnapshot) {
        IncomingData incomingData = dataSnapshot.getValue(IncomingData.class);
        incomings.add(new IncomingData(incomingData.getRestaurateur(), incomingData.getCustomer(), incomingData.getAddress(), incomingData.getDate(), incomingData.getHour()));
        incomingAdapter = new IncomingAdapter(getContext(), incomings);
        recyclerView.setAdapter(incomingAdapter);
    }

    // TODO: modify the research method in order to remove the item by id and not by restaurateur (this can be implemented after insertion on restaurateur)
    private void removeIncoming(DataSnapshot dataSnapshot) {
        IncomingData incomingData = dataSnapshot.getValue(IncomingData.class);
        for (int i = 0; i < incomings.size(); i++) {
            if (incomings.get(i).getRestaurateur().equals(incomingData.getRestaurateur())) {
                incomings.remove(i);
            }
        }
        incomingAdapter.notifyDataSetChanged();
        incomingAdapter = new IncomingAdapter(getContext(), incomings);
        recyclerView.setAdapter(incomingAdapter);
    }
}
