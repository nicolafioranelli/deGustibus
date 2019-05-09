package com.madness.restaurant.reservations;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ReservationFragment class
 */
public class ReservationFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private ValueEventListener emptyListener;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseUser user;

    public ReservationFragment() {
        // Required empty public constructor
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

        final View rootView = inflater.inflate(R.layout.fragment_reservations, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Reservations));

        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        final Query query = databaseReference.child("orders").orderByChild("restaurantID").equalTo(user.getUid());

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

                return new ReservationHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ReservationHolder holder, final int position, @NonNull ReservationClass model) {
                /* Set the name of the customer */
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

                holder.description.setText(model.getDescription());
                holder.price.setText(model.getTotalPrice());
                holder.date.setText(model.getDeliveryDate());
                holder.hour.setText(model.getDeliveryHour());
                holder.status.setText(model.getStatus());
                if (model.getStatus().equals("new")) {
                    holder.refuse.setVisibility(View.VISIBLE);
                    holder.button.setVisibility(View.VISIBLE);
                    holder.status.setText(R.string.status_new);
                    holder.refuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refuse(position);
                        }
                    });
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accept(position);
                        }
                    });
                } else if (model.getStatus().equals("refused")) {
                    holder.status.setText(R.string.status_refused);
                } else if (model.getStatus().equals("incoming")) {
                    holder.status.setText(R.string.status_elaboration);
                } else if (model.getStatus().equals("done")) {
                    holder.status.setText(R.string.status_done);
                } else if (model.getStatus().equals("delivering")) {
                    holder.status.setText(getString(R.string.status_deliverying));
                } else if (model.getStatus().equals("elaboration")) {
                    holder.status.setText(R.string.status_elaboration);
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

    /* Here is set the click listener on the floating button */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void refuse(final int position) {
        Query refuseQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

        refuseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Set order as refused */
                    databaseReference.child("restaurants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void accept(final int position) {
        Query selectDeliveryman = databaseReference.child("riders").limitToFirst(1);

        selectDeliveryman.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    /* Rider selection */
                    String temp = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        temp = snapshot.getKey();

                    }
                    final String riderID = temp;

                    Query updateQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());
                    updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            objectMap.put("status", "incoming");
                            objectMap.put("riderID", riderID);
                            databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);
                            incomingNotifications(objectMap.get("restaurantID").toString(), riderID, objectMap.get("customerID").toString(), adapter.getRef(position).getKey());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    /* No riders available error */
                    Query updateQuery = databaseReference.child("orders").child(adapter.getRef(position).getKey());

                    updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            databaseReference.child("restaurants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                        objectMap.put("status", "done");
                                        databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);

                                        /* Send notification to user */
                                        final Map<String, Object> newNotification = new HashMap<String, Object>();
                                        newNotification.put("type", getString(R.string.typeNot_noRider));

                                        Map<String, Object> restaurantMap = (HashMap<String, Object>) snapshot.getValue();
                                        String restaurantName = restaurantMap.get("name").toString();
                                        newNotification.put("description", getString(R.string.desc1) + restaurantName + getString(R.string.desc2));

                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        Date date = new Date();
                                        newNotification.put("date", dateFormat.format(date));

                                        databaseReference.child("notifications").child(objectMap.get("customerID").toString()).push().setValue(newNotification);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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

    private void incomingNotifications(String restaurantID, final String riderID, final String customerID, final String orderID) {
        databaseReference.child("restaurants").child(restaurantID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    /* Send notification to user */
                    final Map<String, Object> newNotification = new HashMap<String, Object>();
                    newNotification.put("type", getString(R.string.typeNot_accepted));

                    Map<String, Object> restaurantMap = (HashMap<String, Object>) snapshot.getValue();
                    String restaurantName = restaurantMap.get("name").toString();
                    newNotification.put("description", getString(R.string.desc3) + orderID.substring(1, 6) + getString(R.string.desc4) + restaurantName);

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    newNotification.put("date", dateFormat.format(date));

                    databaseReference.child("notifications").child(customerID).push().setValue(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            /* Send notification to rider */
                            final Map<String, Object> notificationRider = new HashMap<String, Object>();
                            notificationRider.put("type", getString(R.string.typeNot_incoming));
                            notificationRider.put("description", getString(R.string.desc5) + orderID.substring(1, 6) + getString(R.string.desc6));
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            notificationRider.put("date", dateFormat.format(date));

                            databaseReference.child("notifications").child(riderID).push().setValue(notificationRider);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}