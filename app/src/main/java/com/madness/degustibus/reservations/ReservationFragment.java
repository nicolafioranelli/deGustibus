package com.madness.degustibus.reservations;

import android.content.Context;
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
import com.madness.degustibus.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservationFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;
    private ValueEventListener emptyListener;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;
    private HashMap<String, Object> map;

    public ReservationFragment() {
        // Required empty public constructor();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_orders));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("orders").orderByChild("customerID").equalTo(user.getUid());

        FirebaseRecyclerOptions<ReservationData> options =
                new FirebaseRecyclerOptions.Builder<ReservationData>()
                        .setQuery(query, ReservationData.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ReservationData, ReservationHolder>(options) {

            @NonNull
            @Override
            public ReservationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.incoming_item, viewGroup, false);
                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                return new ReservationHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ReservationHolder holder, final int position, @NonNull final ReservationData model) {
                holder.date.setText(model.getDeliveryDate());
                holder.hour.setText(model.getDeliveryHour());
                holder.price.setText("€ "+model.getTotalPrice());
                holder.status.setText(model.getStatus());

                databaseReference.child("restaurants").child(model.getRestaurantID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            String restaurantName = objectMap.get("name").toString();
                            String address = objectMap.get("address").toString();
                            holder.restaurant.setText(restaurantName);
                            holder.restaurantAddres.setText(address);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(0 == model.getDeliverymanID().compareTo("null")){
                    holder.deliveryman.setText(R.string.no_rider);
                }else{
                    databaseReference.child("riders").child(model.getDeliverymanID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                String riderName = objectMap.get("name").toString();
                                holder.deliveryman.setText(riderName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }



                if (model.getStatus().equals("incoming")) {
                    holder.status.setText(R.string.status_incoming);
                } else if (model.getStatus().equals("refused")) {
                    holder.status.setText(R.string.status_refused);
                } else if (model.getStatus().equals("done")) {
                    holder.status.setText(R.string.status_done);
                } else if (model.getStatus().equals("elaboration")) {
                    holder.status.setText(R.string.status_elaboration);
                } else if (model.getStatus().equals("delivering")) {
                    holder.status.setText(R.string.status_delivering);
                }

                //setup recieved listener

                holder.recieved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recieveFood(position,model.getRestaurantID());
                    }
                });

                rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
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
        databaseReference.removeEventListener(emptyListener);
    }

    private void recieveFood(final int position, final String restaurantID) {

        Query updateQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as done */

                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    objectMap.put("status", "done");
                    databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                    /* Send notification to user */
                    final Map<String, Object> newNotification = new HashMap<String, Object>();
                    newNotification.put("type", getString(R.string.typeNot_done));

                    newNotification.put("description", getString(R.string.desc3) + adapter.getRef(position).getKey().substring(1, 6) + getString(R.string.desc9));

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    newNotification.put("date", dateFormat.format(date));

                    databaseReference.child("notifications").child(restaurantID).push().setValue(newNotification);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
