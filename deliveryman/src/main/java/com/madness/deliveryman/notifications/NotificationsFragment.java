package com.madness.deliveryman.notifications;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.madness.deliveryman.R;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ValueEventListener listener;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        getActivity().setTitle(getString(R.string.title_Notifications));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("notifications").child(user.getUid());

        FirebaseRecyclerOptions<NotificationsClass> options =
                new FirebaseRecyclerOptions.Builder<NotificationsClass>()
                        .setQuery(query, NotificationsClass.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<NotificationsClass, NotificationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, final int position, @NonNull NotificationsClass model) {
                holder.type.setText(model.getType());
                holder.description.setText(model.getDescription());
                holder.type.setText(model.getType());

                /* Sets a click listener on the corresponding delete button of the notification */
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove(position);
                    }
                });
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.notifications_listitem, viewGroup, false);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                return new NotificationHolder(view);
            }
        };

        /* Listener to check if the recycler view is empty */
        listener = query.addValueEventListener(new ValueEventListener() {
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

        recyclerView.setAdapter(adapter);
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

    @Override
    public void onDetach() {
        super.onDetach();
        databaseReference.removeEventListener(listener);
    }

    private void remove(int position) {
        /* Remove query */
        Query removeQuery = databaseReference.child("notifications")
                .child(user.getUid())
                .orderByKey()
                .equalTo(adapter.getRef(position).getKey());

        /* Event listener for the delete process */
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
    }
}