package com.madness.restaurant.reservations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;
import com.madness.restaurant.haversine.ComputeDistance;
import com.madness.restaurant.haversine.Point;

import org.json.JSONException;
import org.json.JSONObject;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderChoiceFragment extends Fragment {

    /* Database references */
    private DatabaseReference databaseReference;
    private DatabaseReference summaryReference;
    private DatabaseReference customerReference;
    private DatabaseReference riderReference;

    /* Value Event Listeners */
    private ValueEventListener summaryListener;
    private ValueEventListener locationListener;
    private ValueEventListener customerListener;
    private ValueEventListener riderListener;
    private GeoQueryEventListener eventListener;

    /* Firebase and GeoFire */
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private Geocoder geocoder;
    private GeoQuery geoQuery;

    private LinearLayoutManager linearLayoutManager;
    private RiderAdapter adapter;
    private Point restaurant;
    private RiderComparable rider;
    private boolean isNew;
    private View view;
    private ReservationClass order;
    private CircleImageView riderImage;
    private TextView riderName;
    private RatingBar riderRatingBar;
    private EditText riderComment;
    private Button reviewButton;

    public RiderChoiceFragment() {
        // Required empty public constructor
    }

    /* In the onCreate method all variables containing useful information are set in a way that other
     * methods can use them once called.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        //order = new OrderData();
    }

    /* The onCreateView allows to inflate the view of the fragment, in particular here are load information
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

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRatings();
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            summaryReference.removeEventListener(summaryListener);
            if (eventListener != null) {
                geoQuery.removeAllListeners();
            }
            databaseReference.child("restaurants").child(user.getUid()).removeEventListener(locationListener);
            customerReference.removeEventListener(customerListener);
            riderReference.removeEventListener(riderListener);
        } catch (Exception e) {
            Log.e("MAD", "onDetach: ", e);
        }
    }

    /* Retrieve data from Firebase and load it into the summary at the beginning of the fragment */
    private void loadData(final String id, final View view, final isNewCallback callback) {
        /* Items of the view are here retrieved to be populated during the firebase request */
        final TextView status = view.findViewById(R.id.status);
        final TextView customer = view.findViewById(R.id.customer);
        final TextView description = view.findViewById(R.id.description);
        final TextView date = view.findViewById(R.id.date);
        final TextView hour = view.findViewById(R.id.hour);
        final TextView price = view.findViewById(R.id.price);
        final Button button = view.findViewById(R.id.orderButton);
        final Button refuse = view.findViewById(R.id.refuseButton);

        // review items
        riderImage = view.findViewById(R.id.delImage);
        riderName = view.findViewById(R.id.deliverymanName);
        riderRatingBar = view.findViewById(R.id.ratingBar);
        riderComment = view.findViewById(R.id.comment);
        reviewButton = view.findViewById(R.id.reviewButton);

        summaryReference = databaseReference.child("orders").child(id);
        summaryListener = summaryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ItemClass> cart = new ArrayList<>();
                for (DataSnapshot obj : dataSnapshot.child("cart").getChildren()) {
                    ItemClass item = new ItemClass(Integer.parseInt(obj.child("quantity").getValue().toString()),
                            obj.getKey(),
                            obj.child("name").getValue().toString(),
                            Integer.parseInt(obj.child("rating").getValue().toString())
                    );
                    cart.add(item);
                }

                order = new ReservationClass(dataSnapshot.getKey(),
                        dataSnapshot.child("customerID").getValue().toString(),
                        dataSnapshot.child("restaurantID").getValue().toString(),
                        dataSnapshot.child("deliverymanID").getValue().toString(),
                        dataSnapshot.child("deliveryDate").getValue().toString(),
                        dataSnapshot.child("deliveryHour").getValue().toString(),
                        dataSnapshot.child("totalPrice").getValue().toString(),
                        dataSnapshot.child("customerAddress").getValue().toString(),
                        dataSnapshot.child("restaurantAddress").getValue().toString(),
                        dataSnapshot.child("status").getValue().toString(),
                        dataSnapshot.child("riderComment").getValue().toString(),
                        dataSnapshot.child("riderRating").getValue().toString(),
                        dataSnapshot.child("restaurantComment").getValue().toString(),
                        dataSnapshot.child("restaurantRating").getValue().toString(),
                        cart
                );


                /* Set the name of the customer */
                customerReference = databaseReference.child("customers").child(order.getCustomerID());
                customerListener = customerReference.addValueEventListener(new ValueEventListener() {
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
                StringBuilder listOfDishes = new StringBuilder();
                for(int i=0; i<order.getCart().size(); i++) {
                    listOfDishes.append(order.getCart().get(i).getQuantity());
                    listOfDishes.append(" x ");
                    listOfDishes.append(order.getCart().get(i).getName());
                    listOfDishes.append("\n");
                }
                description.setText(listOfDishes.toString());
                price.setText(order.getTotalPrice());
                date.setText(order.getDeliveryDate());
                hour.setText(order.getDeliveryHour());
                if (order.getStatus().equals("new")) {
                    refuse.setVisibility(View.VISIBLE);
                    status.setText(R.string.status_new);
                    refuse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NewNotificationClass notification = new NewNotificationClass(getContext());
                            notification.refuseAndNotify(order);
                            view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                            view.findViewById(R.id.recyclerView).setVisibility(View.GONE);
                        }
                    });
                    isNew = true;
                } else if (order.getStatus().equals("refused")) {
                    refuse.setVisibility(View.GONE);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    status.setText(R.string.status_refused);
                    status.setTextColor(Color.RED);
                    isNew = false;
                } else if (order.getStatus().equals("incoming")) {
                    refuse.setVisibility(View.GONE);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    status.setText(R.string.status_elaboration);
                    status.setTextColor(getResources().getColor(R.color.theme_colorAccent));
                    isNew = false;
                } else if (order.getStatus().equals("done")) {
                    refuse.setVisibility(View.GONE);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    view.findViewById(R.id.reviews).setVisibility(View.VISIBLE);
                    status.setText(R.string.status_done);
                    isNew = false;

                    if(!dataSnapshot.child("restRiderRating").exists()) {
                        loadRider(order.getDeliverymanID(), 0);
                    } else {
                        loadRider(order.getDeliverymanID(), Math.round(Float.parseFloat(dataSnapshot.child("restRiderRating").getValue(String.class))));
                    }
                    if (dataSnapshot.child("restRiderRating").exists()) {
                        reviewButton.setVisibility(View.GONE);
                        riderRatingBar.setClickable(false);
                        riderComment.setFocusable(false);
                        riderComment.setEnabled(false);
                        riderComment.setText(dataSnapshot.child("restRiderComment").getValue(String.class));
                    }
                } else if (order.getStatus().equals("delivering")) {
                    refuse.setVisibility(View.GONE);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    status.setText(getString(R.string.status_deliverying));
                    status.setTextColor(getResources().getColor(R.color.theme_colorTertiary));
                    isNew = false;
                } else if (order.getStatus().equals("elaboration")) {
                    refuse.setVisibility(View.GONE);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    status.setText(R.string.status_elaboration);
                    status.setTextColor(getResources().getColor(R.color.colorAccent));
                    isNew = false;
                }

                /* Give two different behaviours to the same fragment: if isNew is true it will display
                 * the section related to the riders choice, while if the order is not a new order it will
                 * be simply a review of the past order.
                 */
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

    private void loadRider (String riderID, Integer riderRating) {
        databaseReference.child("riders").child(riderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photo = null;
                if(dataSnapshot.child("photo").exists()) {
                    photo = dataSnapshot.child("photo").getValue().toString();
                }

                GlideApp.with(getContext())
                        .load(photo)
                        .placeholder(R.drawable.user_profile)
                        .into(riderImage);

                riderName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        riderRatingBar.setRating(riderRating);
        riderRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = riderRatingBar.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    int stars = (int) starsf + 1;
                    riderRatingBar.setRating(stars);
                    v.setPressed(false);
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setPressed(true);
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setPressed(false);
                }
                return true;
            }
        });
    }

    private void updateRatings () {
        Boolean ok = true;

        if (riderRatingBar.getRating() == 0) {
            ok = false;
        }

        if (ok) {
            /* Update ratings */

            FirebaseDatabase.getInstance().getReference().child("restaurants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> riderRating = new HashMap<>();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    riderRating.put("name", dataSnapshot.child("name").getValue().toString());
                    riderRating.put("comment", riderComment.getText().toString());
                    riderRating.put("date", dateFormat.format(date));
                    riderRating.put("rating", riderRatingBar.getRating());
                    databaseReference.child("ratings").child("riders").child(order.getDeliverymanID()).push().updateChildren(riderRating);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            /* Update rider */
            final DatabaseReference refRider = databaseReference.child("riders").child(order.getDeliverymanID());
            refRider.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object rating = dataSnapshot.child("rating").getValue();
                    Object count = dataSnapshot.child("count").getValue();
                    refRider.child("rating").setValue(Integer.parseInt(rating.toString()) + Math.round(riderRatingBar.getRating()));
                    refRider.child("count").setValue(Integer.parseInt(count.toString()) + 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put("restRiderComment", riderComment.getText().toString());
            updateMap.put("restRiderRating", Float.toString(riderRatingBar.getRating()));
            databaseReference.child("orders").child(order.getId()).updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.evaluation_Title))
                            .setMessage(getString(R.string.evaluation_Desc))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    reviewButton.setVisibility(View.GONE);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            });

        } else {
            Toast.makeText(getContext(), getString(R.string.evaluation_Err), Toast.LENGTH_SHORT);
        }
    }

    /* This method allows to download data from Firebase through different calls to Event Listeners
     * at the end the custom Adapter is populated and are present also some methods for data change/delete.
     */
    private void loadAdapter() {
        /* Get all the riders in a certain range (here defined in 5 kilometers) */
        getRiders(new GetRidersCallback() {
            /* Save the riders in a List of RiderComparable desc */
            List<RiderComparable> list = new ArrayList<>();
            HashMap<String, GeoLocation> locations = new HashMap<>();

            @Override
            public void onCallback(RiderComparable rider, GeoLocation location) {
                boolean exists = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals(rider.getName())) {
                        exists = true;
                        list.set(i, rider);
                        locations.put(rider.getName(), location);
                    }
                }

                if(exists) {
                    adapter.updateData(list);
                } else {
                    list.add(rider);
                    locations.put(rider.getName(), location);
                    /* Sort the riders in the list according to an ascending order (distance) */
                    Collections.sort(list, new Comparator<RiderComparable>() {
                        public int compare(RiderComparable obj1, RiderComparable obj2) {
                            // ## Ascending order
                            return Double.valueOf(obj1.getDistance()).compareTo(Double.valueOf(obj2.getDistance()));
                        }
                    });
                    /* Set the adapter and show the recycler view while make invisible the progress bar */
                    adapter = new RiderAdapter(getContext(), view, list, order, locations);
                    recyclerView.setAdapter(adapter);
                    view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                }

            }

            /* On update method finds the rider which was update (or has been inserted in the GeoFire radius */
            @Override
            public void onUpdate(RiderComparable rider) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals(rider.getName())) {
                        list.set(i, rider);
                    }
                }
                Collections.sort(list, new Comparator<RiderComparable>() {
                    public int compare(RiderComparable obj1, RiderComparable obj2) {
                        return Double.valueOf(obj1.getDistance()).compareTo(Double.valueOf(obj2.getDistance()));
                    }
                });
                /* This method of the adapter reload all the riders in order to update current shown items */
                adapter.updateData(list);
            }

            /* On exit method is called once a rider goes out of the GeoFire radius */
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

    /* The convertLocation method is used to convert the restaurant address (written) into a set of GPS
     * coordinates which are obtained through a callback after a query on the database
     */
    private void convertLocation(final RestPositionCallback callback) {
        locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> rest = (HashMap<String, Object>) dataSnapshot.getValue();
                callback.onCallback(rest.get("address").toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.child("restaurants").child(user.getUid()).addValueEventListener(locationListener);
    }

    /* This method retrieves data about the riders */
    private void retrieveData(String key, final DataRetrieveCallback callback) {
        System.out.println("Key2: " + key);
        riderReference = databaseReference.child("riders").child(key);
        riderListener = riderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                Map<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                callback.onCallback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /* This method retrieves all the riders which are in the GeoFire radius */
    private void getRiders(final GetRidersCallback callback) {
        GeoFire geoFire = new GeoFire(databaseReference.child("positions"));
        /* This GeoQuery retrieves all the riders whose distance is in 5 kilometers from the restaurant location */
        geoQuery = geoFire.queryAtLocation(new GeoLocation(restaurant.getLatitude(), restaurant.getLongitude()), 5);

        eventListener = new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                System.out.println("Key: " + key);
                retrieveData(key, new DataRetrieveCallback() {
                    @Override
                    public void onCallback(Map user) {
                            /* This method retrieves the information of the rider and will add them to the item to be passed to the adapter */
                            rider = null;
                            rider = new RiderComparable();
                            rider.setAvailable((boolean) user.get("available"));
                            rider.setName(user.get("name").toString());
                            try {
                                rider.setPhoto(user.get("photo").toString());
                            } catch (Exception e) {
                                rider.setPhoto("default");
                            }
                            rider.setKey(key);
                            Point customer = new Point();
                            customer.setLatitude(location.latitude);
                            customer.setLongitude(location.longitude);

                            // Get haversine class and call method to calculate distance, then display it on the recycler view
                            ComputeDistance computeDistance = new ComputeDistance();
                            rider.setDistance(computeDistance.getDistance(customer, restaurant));

                            try {
                                rider.setCount(Integer.valueOf(user.get("count").toString()));
                                rider.setRating(Float.valueOf(user.get("rating").toString()));
                            }catch (Exception e){
                                rider.setCount(0);
                                rider.setRating(0);
                            }


                            callback.onCallback(rider, location);
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                /* Case where a rider exits the geofire radius */
                rider = null;
                rider = new RiderComparable();
                rider.setKey(key);
                callback.onExit(rider);
            }

            @Override
            public void onKeyMoved(final String key, final GeoLocation location) {
                /* Same thing as key entered is performed since is similar to a new incoming but the callback
                 * method is another because it just update that modified rider.
                 */
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

    /* Interfaces for callbacks */
    public interface RestPositionCallback {
        void onCallback(String value);
    }

    public interface DataRetrieveCallback {
        void onCallback(Map user);
    }

    public interface GetRidersCallback {
        void onCallback(RiderComparable rider, GeoLocation location);

        void onUpdate(RiderComparable rider);

        void onExit(RiderComparable rider);
    }

    public interface isNewCallback {
        void onCallback(boolean value);
    }
}
