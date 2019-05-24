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
import com.madness.deliveryman.R;

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
    private Maps mapsInterface;
    private HashMap<String, Object> map;

    public ReservationFragment() {
        // Required empty public constructor();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Maps){
            mapsInterface = (Maps) context;
        }else{
            throw new ClassCastException(context.toString() + "must implement maplistener");
        }
    }

    /* During the creation of the view the title is set and layout is generated */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Incoming));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("orders").orderByChild("deliverymanID").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                holder.customerAddress.setText(model.getCustomerAddress());
                holder.price.setText("€ "+model.getTotalPrice());

                databaseReference.child("customers").child(model.getCustomerID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            String customerName = objectMap.get("name").toString();
                            holder.customer.setText(customerName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

                if(model.getStatus().equals("incoming")){
                    holder.map.setVisibility(View.GONE);
                }
                else if(model.getStatus().equals("elaboration")){
                    // show the position of the restaurant
                    holder.map.setVisibility(View.VISIBLE);
                    holder.map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mapsInterface.callMaps(holder.restaurant.getText().toString(),holder.restaurantAddres.getText().toString());
                        }
                    });
                }else if(model.getStatus().equals("delivering")){
                    // show the position of the customer
                    holder.map.setVisibility(View.VISIBLE);
                    holder.map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mapsInterface.callMaps(holder.customer.getText().toString(),holder.customerAddress.getText().toString());
                        }
                    });
                }else if (model.getStatus().equals("done")||model.getStatus().equals("refused")){
                    // hide the map
                    holder.map.setVisibility(View.GONE);
                }





                holder.status.setText(model.getStatus());

                if (model.getStatus().equals("incoming")) {
                    holder.refuse.setVisibility(View.VISIBLE);
                    holder.button.setVisibility(View.VISIBLE);
                    holder.status.setText(R.string.status_incoming);
                    holder.refuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refuse(position, model.getRestaurantID());
                        }
                    });
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accept(position, model.getRestaurantID());
                        }
                    });
                } else if (model.getStatus().equals("refused")) {
                    holder.status.setText(R.string.status_refused);
                    holder.refuse.setVisibility(View.GONE);
                    holder.button.setVisibility(View.GONE);
                } else if (model.getStatus().equals("done")) {
                    holder.status.setText(R.string.status_done);
                    holder.refuse.setVisibility(View.GONE);
                    holder.button.setVisibility(View.GONE);
                } else if (model.getStatus().equals("elaboration")) {
                    holder.status.setText(R.string.status_elaboration);
                    holder.button.setVisibility(View.GONE);
                    holder.refuse.setVisibility(View.VISIBLE);
                    holder.refuse.setText(R.string.buttonPick);
                    holder.refuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pick(position, model.getCustomerID());
                        }
                    });
                } else if (model.getStatus().equals("delivering")) {
                    holder.status.setText(R.string.status_delivering);
                    holder.button.setVisibility(View.GONE);
                    holder.refuse.setVisibility(View.GONE);
                    holder.refuse.setText(R.string.buttonDeliver);
                }
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

    private void refuse(final int position, final String restaurantID) {
        Query refuseQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

        refuseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as refused */
                    databaseReference.child("restaurants").child(restaurantID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                objectMap.put("status", "refused");
                                databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                                /* Send notification to user */
                                final Map<String, Object> newNotification = new HashMap<String, Object>();
                                newNotification.put("type", getString(R.string.typeNot_refused));

                                Map<String, Object> restaurantMap = (HashMap<String, Object>) snapshot.getValue();
                                String restaurantName = restaurantMap.get("name").toString();
                                newNotification.put("description", getString(R.string.desc1) + restaurantName);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                newNotification.put("date", dateFormat.format(date));

                                databaseReference.child("notifications").child(objectMap.get("customerID").toString()).push().setValue(newNotification);

                                final Map<String, Object> restaurantNotification = new HashMap<String, Object>();
                                restaurantNotification.put("type", getString(R.string.typeNot_refused));
                                restaurantNotification.put("description", getString(R.string.desc1));
                                restaurantNotification.put("date", dateFormat.format(date));

                                databaseReference.child("notifications").child(restaurantID).push().setValue(restaurantNotification);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void accept(final int position, final String restaurantID) {
        Query updateQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as elaboration */
                    databaseReference.child("riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                objectMap.put("status", "elaboration");
                                databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                                /* Send notification to user */
                                final Map<String, Object> newNotification = new HashMap<String, Object>();
                                newNotification.put("type", getString(R.string.typeNot_accepted));

                                Map<String, Object> rider = (HashMap<String, Object>) snapshot.getValue();
                                String riderName = rider.get("name").toString();
                                newNotification.put("description", getString(R.string.desc7) + riderName);

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pick(final int position, final String customerID) {

        Query updateQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

        updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as elaboration */
                    databaseReference.child("riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                objectMap.put("status", "delivering");
                                databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                                /* Send notification to user */
                                final Map<String, Object> newNotification = new HashMap<String, Object>();
                                newNotification.put("type", getString(R.string.typeNot_deliverying));

                                Map<String, Object> rider = (HashMap<String, Object>) snapshot.getValue();
                                String riderName = rider.get("name").toString();
                                newNotification.put("description", getString(R.string.desc8) + riderName);

                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                newNotification.put("date", dateFormat.format(date));

                                databaseReference.child("notifications").child(customerID).push().setValue(newNotification);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deliver(final int position, final String restaurantID) {

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

    public interface Maps{
        public void callMaps(String name, String address);
    }

}
