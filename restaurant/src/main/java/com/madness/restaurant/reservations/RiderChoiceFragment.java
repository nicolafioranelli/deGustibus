package com.madness.restaurant.reservations;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.madness.restaurant.haversine.ComputeDistance;
import com.madness.restaurant.haversine.Point;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RiderChoiceFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ValueEventListener summaryListener;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RiderAdapter adapter;
    private Point restaurant;
    private RiderComparable rider;
    private Geocoder geocoder;
    private GeoQueryEventListener eventListener;
    private GeoQuery geoQuery;
    private boolean isNew;
    private View view;
    private ValueEventListener location;
    private ValueEventListener custName;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private ValueEventListener data;
    private DatabaseReference reference3;

    public RiderChoiceFragment() {
        // Required empty public constructor
    }

    /* In the onCreate method all variables containing useful informations are set in a way that other
     * methods can use them once called.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    /* The onCreateView allows to inflate the view of the fragment, in particular here are load informations
     * from Firebase related to the order and the riders in case is a new order.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_rider_choice, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Order));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        view = rootView;

        String orderID = this.getArguments().getString("orderID");
        /* Here data about the order is loaded into the first part of the fragment, then if loadData
         * returns true (this happens in case is a new order) the part related to the rider choice is
         * displayed and computed, else no other operation is perfomed.
         */
        loadData(orderID, rootView, new isNewCallback() {
            @Override
            public void onCallback(boolean value) {
                if (value) {
                    convertLocation(new RestPositionCallback() {
                        @Override
                        public void onCallback(String value) {
                            geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> fromLocationName = null;
                            Double latitude = null;
                            Double longitude = null;
                            try {
                                fromLocationName = geocoder.getFromLocationName(value, 1);
                                if (fromLocationName != null && fromLocationName.size() > 0) {
                                    Address a = fromLocationName.get(0);
                                    latitude = a.getLatitude();
                                    longitude = a.getLongitude();

                                    restaurant = new Point();
                                    restaurant.setLatitude(latitude);
                                    restaurant.setLongitude(longitude);
                                }
                            } catch (Exception e) {
                                Log.e("MAD", "onCallback: ", e);
                            }
                            loadAdapter();
                        }
                    });
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reference.removeEventListener(summaryListener);
        if (eventListener != null) {
            geoQuery.removeAllListeners();
        }
        databaseReference.child("restaurants").child(user.getUid()).removeEventListener(location);
        reference2.removeEventListener(custName);
        reference3.addValueEventListener(data);
    }

    /* Retrieve data from Firebase and load it into the summary at the beginning of the fragment */
    private void loadData(final String id, final View view, final isNewCallback callback) {
        final TextView status = view.findViewById(R.id.status);
        final TextView customer = view.findViewById(R.id.customer);
        final TextView description = view.findViewById(R.id.description);
        final TextView date = view.findViewById(R.id.date);
        final TextView hour = view.findViewById(R.id.hour);
        final TextView price = view.findViewById(R.id.price);
        final Button button = view.findViewById(R.id.orderButton);
        final Button refuse = view.findViewById(R.id.refuseButton);

        reference = databaseReference.child("orders").child(id);
        summaryListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> orderData = (HashMap<String, Object>) dataSnapshot.getValue();

                /* Set the name of the customer */
                reference2 = databaseReference.child("customers").child(orderData.get("customerID").toString());
                custName = reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            String customerName = objectMap.get("name").toString();
                            customer.setText(customerName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                /* Set other fields and update correctly the current status */
                description.setText(orderData.get("description").toString());
                price.setText(orderData.get("totalPrice").toString());
                date.setText(orderData.get("deliveryDate").toString());
                hour.setText(orderData.get("deliveryHour").toString());
                if (orderData.get("status").toString().equals("new")) {
                    refuse.setVisibility(View.VISIBLE);
                    //button.setVisibility(View.VISIBLE);
                    status.setText(R.string.status_new);
                    refuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refuse(id);
                        }
                    });
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accept(id);
                        }
                    });
                    isNew = true;
                } else if (orderData.get("status").toString().equals("refused")) {
                    refuse.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    status.setText(R.string.status_refused);
                    isNew = false;
                } else if (orderData.get("status").toString().equals("incoming")) {
                    refuse.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    status.setText(R.string.status_elaboration);
                    isNew = false;
                } else if (orderData.get("status").toString().equals("done")) {
                    refuse.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    status.setText(R.string.status_done);
                    isNew = false;
                } else if (orderData.get("status").toString().equals("delivering")) {
                    refuse.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    status.setText(getString(R.string.status_deliverying));
                    isNew = false;
                } else if (orderData.get("status").toString().equals("elaboration")) {
                    refuse.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    status.setText(R.string.status_elaboration);
                    isNew = false;
                }

                if (isNew) {
                    callback.onCallback(isNew);
                } else {
                    view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    callback.onCallback(isNew);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadAdapter() {
        getRiders(new GetRidersCallback() {
            List<RiderComparable> list = new ArrayList<>();

            @Override
            public void onCallback(RiderComparable rider) {
                list.add(rider);
                Collections.sort(list, new Comparator<RiderComparable>() {
                    public int compare(RiderComparable obj1, RiderComparable obj2) {
                        // ## Ascending order
                        return Double.valueOf(obj1.getDistance()).compareTo(Double.valueOf(obj2.getDistance())); // To compare integer values
                    }
                });
                adapter = new RiderAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);
                view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
            }

            @Override
            public void onUpdate(RiderComparable rider) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals(rider.getName())) {
                        list.set(i, rider);
                    }
                }
                Collections.sort(list, new Comparator<RiderComparable>() {
                    public int compare(RiderComparable obj1, RiderComparable obj2) {
                        // ## Ascending order
                        return Double.valueOf(obj1.getDistance()).compareTo(Double.valueOf(obj2.getDistance())); // To compare integer values
                    }
                });
                adapter.updateData(list);
            }

            @Override
            public void onExit(RiderComparable rider) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getKey().equals(rider.getKey())) {
                        list.remove(i);
                    }
                }
                adapter.updateData(list);
            }
        });

    }

    private void refuse(final String orderID) {
        Query refuseQuery = databaseReference.child("orders").child(orderID);

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

    private void accept(final String orderID) {
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

                    Query updateQuery = databaseReference.child("orders").child(orderID);
                    updateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            objectMap.put("status", "incoming");
                            objectMap.put("deliverymanID", riderID);
                            databaseReference.child("orders").child(dataSnapshot.getKey()).updateChildren(objectMap);
                            incomingNotifications(objectMap.get("restaurantID").toString(), riderID, objectMap.get("customerID").toString(), orderID);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    /* No riders available error */
                    Query updateQuery = databaseReference.child("orders").child(orderID);

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

    private void convertLocation(final RestPositionCallback callback) {
        location = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> rest = (HashMap<String, Object>) dataSnapshot.getValue();
                callback.onCallback(rest.get("address").toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.child("restaurants").child(user.getUid()).addValueEventListener(location);
    }

    private void retrieveData(String key, final DataRetrieveCallback callback) {
        reference3 = databaseReference.child("riders").child(key);
        data = reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                callback.onCallback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRiders(final GetRidersCallback callback) {
        GeoFire geoFire = new GeoFire(databaseReference.child("positions"));
        geoQuery = geoFire.queryAtLocation(new GeoLocation(restaurant.getLatitude(), restaurant.getLongitude()), 5);

        eventListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                retrieveData(key, new DataRetrieveCallback() {
                    @Override
                    public void onCallback(Map user) {
                        rider = null;
                        rider = new RiderComparable();
                        rider.setAvailable((boolean) user.get("available"));
                        rider.setName(user.get("name").toString());
                        rider.setPhoto(user.get("photo").toString());
                        rider.setKey(key);
                        Point customer = new Point();
                        customer.setLatitude(location.latitude);
                        customer.setLongitude(location.longitude);

                        // Get haversine class and call method to calculate distance, then display it on the recycler view
                        ComputeDistance computeDistance = new ComputeDistance();
                        rider.setDistance(computeDistance.getDistance(customer, restaurant));

                        callback.onCallback(rider);
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                rider = null;
                rider = new RiderComparable();
                rider.setKey(key);
                callback.onExit(rider);
            }

            @Override
            public void onKeyMoved(final String key, final GeoLocation location) {
                retrieveData(key, new DataRetrieveCallback() {
                    @Override
                    public void onCallback(Map user) {
                        rider = null;
                        rider = new RiderComparable();
                        rider.setAvailable((boolean) user.get("available"));
                        rider.setName(user.get("name").toString());
                        rider.setPhoto(user.get("photo").toString());
                        rider.setKey(key);
                        Point customer = new Point();
                        customer.setLatitude(location.latitude);
                        customer.setLongitude(location.longitude);

                        // Get haversine class and call method to calculate distance, then display it on the recycler view
                        ComputeDistance computeDistance = new ComputeDistance();
                        rider.setDistance(computeDistance.getDistance(customer, restaurant));

                        callback.onUpdate(rider);
                    }
                });
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        };

        geoQuery.addGeoQueryEventListener(eventListener);
    }

    public interface RestPositionCallback {
        void onCallback(String value);
    }

    public interface DataRetrieveCallback {
        void onCallback(Map user);
    }

    public interface GetRidersCallback {
        void onCallback(RiderComparable rider);

        void onUpdate(RiderComparable rider);

        void onExit(RiderComparable rider);
    }

    public interface isNewCallback {
        void onCallback(boolean value);
    }
}
