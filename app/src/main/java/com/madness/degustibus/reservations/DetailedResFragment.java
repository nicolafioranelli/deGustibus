package com.madness.degustibus.reservations;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
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
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.madness.degustibus.GlideApp;
import com.madness.degustibus.R;
import com.madness.degustibus.order.ProfileClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailedResFragment extends Fragment {

    /* Firebase */
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userReference;
    private DatabaseReference orderReference;
    private ValueEventListener userListener;
    private ValueEventListener orderListener;
    private FirebaseRecyclerAdapter adapter;

    /* Widgets */
    private StateProgressBar stateProgressBar;
    private View view;
    private TextView status;
    private Button button;
    private TextView restaurant;
    private TextView deliveryman;
    private TextView date;
    private TextView hour;
    private TextView price;
    private RecyclerView recyclerView;
    private Button reviewButton;
    private CircleImageView restImage;
    private TextView restName;
    private RatingBar ratingBar2;
    private EditText comment;
    private CircleImageView riderImage;
    private TextView riderName;
    private RatingBar riderRatingBar;
    private EditText riderComment;

    /* Data */
    private ProfileClass userProfile;
    private OrderClass order;
    private List<ItemClass> dishes;

    /* Algolia */
    private Client client;
    private Index index;

    public DetailedResFragment() {
        // Required empty public constructor
    }

    /* Lifecycle */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        client = new Client("LRBUKD1XJR", "d796532dfd54cafdf4587b412ad560f8");
        index = client.getIndex("rest_HOME");
        dishes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_detailed_res, container, false);
        getActivity().setTitle(getString(R.string.title_Details));
        view = rootView;
        final String[] labels = {getString(R.string.step1), getString(R.string.step2), getString(R.string.step3), getString(R.string.step4)};

        stateProgressBar = rootView.findViewById(R.id.progressBar);
        status = rootView.findViewById(R.id.status);
        button = rootView.findViewById(R.id.button);
        restaurant = rootView.findViewById(R.id.restaurant);
        deliveryman = rootView.findViewById(R.id.deliveryman);
        date = rootView.findViewById(R.id.date);
        hour = rootView.findViewById(R.id.hour);
        price = rootView.findViewById(R.id.price);
        stateProgressBar.setStateDescriptionData(labels);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        reviewButton = rootView.findViewById(R.id.reviewButton);
        restImage = rootView.findViewById(R.id.restImage);
        restName = rootView.findViewById(R.id.restaurantName);
        ratingBar2 = rootView.findViewById(R.id.ratingBar);
        comment = rootView.findViewById(R.id.comment);
        riderImage = rootView.findViewById(R.id.riderImage);
        riderName = rootView.findViewById(R.id.riderName);
        riderComment = rootView.findViewById(R.id.riderComment);
        riderRatingBar = rootView.findViewById(R.id.riderRatingBar);

        userReference = databaseReference.child("customers").child(user.getUid());
        userListener = userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(ProfileClass.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadFromFirebase();

        Query query = databaseReference.child("orders").child(getArguments().getString("orderID")).child("cart");
        FirebaseRecyclerOptions<ItemClass> options =
                new FirebaseRecyclerOptions.Builder<ItemClass>()
                        .setQuery(query, new SnapshotParser<ItemClass>() {
                            @NonNull
                            @Override
                            public ItemClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                ItemClass item = new ItemClass(Integer.parseInt(snapshot.child("quantity").getValue().toString()),
                                        snapshot.getKey(),
                                        snapshot.child("name").getValue().toString(),
                                        Integer.parseInt(snapshot.child("rating").getValue().toString())
                                );
                                dishes.add(item);
                                return item;
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<ItemClass, ItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemHolder holder, final int position, @NonNull final ItemClass model) {
                holder.foodName.setText(model.getName());
                holder.quantity.setText(model.getQuantity().toString());
                holder.ratingBar.setRating(model.getRating());
                holder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            float touchPositionX = event.getX();
                            float width = holder.ratingBar.getWidth();
                            float starsf = (touchPositionX / width) * 5.0f;
                            int stars = (int) starsf + 1;
                            holder.ratingBar.setRating(stars);
                            dishes.get(position).setRating(stars);
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

            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View holdView = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.food_listitem, viewGroup, false);
                return new ItemHolder(holdView);
            }
        };
        recyclerView.setAdapter(adapter);

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRatings();
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
        userReference.removeEventListener(userListener);
        orderReference.removeEventListener(orderListener);
        adapter.stopListening();
    }
    // end lifecycle

    /* Helpers */
    private void loadFromFirebase() {
        final String orderID = getArguments().getString("orderID");
        orderReference = databaseReference.child("orders").child(orderID);
        orderListener = orderReference.addValueEventListener(new ValueEventListener() {
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

                order = new OrderClass(dataSnapshot.getKey(),
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

                if (order.getStatus().equals("new")) {
                    status.setText(R.string.status_new);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    button.setVisibility(View.GONE);
                } else if (order.getStatus().equals("incoming")) {
                    status.setText(R.string.status_elaboration);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    button.setVisibility(View.GONE);
                } else if (order.getStatus().equals("refused")) {
                    status.setText(R.string.status_refused);
                    stateProgressBar.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                } else if (order.getStatus().equals("done")) {
                    status.setText(R.string.status_done);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                    button.setVisibility(View.GONE);
                    if(order.getRestaurantRating().equals("null")) {
                        loadRestaurant(order.getRestaurantID(), 0);
                    } else {
                        loadRestaurant(order.getRestaurantID(), Math.round(Float.parseFloat(order.getRestaurantRating())));
                    }
                    if(order.getRiderRating().equals("null")) {
                        loadRider(order.getDeliverymanID(), 0);
                    } else {
                        loadRider(order.getDeliverymanID(), Math.round(Float.parseFloat(order.getRiderRating())));
                    }

                    view.findViewById(R.id.reviews).setVisibility(View.VISIBLE);
                    if (!order.getRestaurantRating().equals("null")) {
                        reviewButton.setVisibility(View.GONE);
                        ratingBar2.setClickable(false);
                        riderRatingBar.setClickable(false);
                        comment.setEnabled(false);
                        comment.setFocusable(false);
                        riderComment.setFocusable(false);
                        riderComment.setEnabled(false);
                    }

                } else if (order.getStatus().equals("elaboration")) {
                    status.setText(R.string.status_elaboration);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    button.setVisibility(View.GONE);
                } else if (order.getStatus().equals("delivering")) {
                    status.setText(R.string.status_delivering);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                    button.setVisibility(View.VISIBLE);
                }

                date.setText(order.getDeliveryDate());
                hour.setText(order.getDeliveryHour());
                price.setText(order.getTotalPrice() + " €");

                if (order.getDeliverymanID().equals("null")) {
                    deliveryman.setText(R.string.no_rider);
                } else {
                    databaseReference.child("riders").child(order.getDeliverymanID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                String riderName = objectMap.get("name").toString();
                                deliveryman.setText(riderName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                view.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        restaurant.setText(getArguments().getString("restaurant"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodReceived();
            }
        });
    }

    private void loadRestaurant(String restID, Integer restRating) {
        databaseReference.child("restaurants").child(restID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                String photo = null;
                if(dataSnapshot.child("photo").exists()) {
                    photo = dataSnapshot.child("photo").getValue().toString();
                }

                GlideApp.with(getContext())
                        .load(photo)
                        .placeholder(R.drawable.restaurant)
                        .into(restImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        restName.setText(getArguments().getString("restaurant"));
        ratingBar2.setRating(restRating);
        ratingBar2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float touchPositionX = event.getX();
                    float width = ratingBar2.getWidth();
                    float starsf = (touchPositionX / width) * 5.0f;
                    int stars = (int) starsf + 1;
                    ratingBar2.setRating(stars);
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

    private void loadRider(String riderID, Integer riderRating) {
        databaseReference.child("riders").child(riderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
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

    private void foodReceived() {
        Query updateQuery = databaseReference.child("orders").child(order.getId());
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
                    newNotification.put("description", getString(R.string.desc3) + order.getId().substring(1, 6) + getString(R.string.desc9));

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    newNotification.put("date", dateFormat.format(date));

                    databaseReference.child("notifications").child(order.getRestaurantID()).push().setValue(newNotification);
                    Map<String, Object> riderMap = new HashMap<>();
                    riderMap.put("available", true);
                    databaseReference.child("riders").child(order.getDeliverymanID()).updateChildren(riderMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Show area about reviews */
        view.findViewById(R.id.reviews).setVisibility(View.VISIBLE);
    }

    private void updateRatings() {
        Boolean ok = true;
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getRating() == 0) {
                ok = false;
            }
        }

        if (ratingBar2.getRating() == 0) {
            ok = false;
        }

        if (riderRatingBar.getRating() == 0) {
            ok = false;
        }

        if (ok) {
            /* Update ratings */
            Map<String, Object> restRating = new HashMap<>();
            restRating.put("name", userProfile.getName());
            restRating.put("comment", comment.getText().toString());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            restRating.put("date", dateFormat.format(date));
            restRating.put("rating", ratingBar2.getRating());
            databaseReference.child("ratings").child("restaurants").child(order.getRestaurantID()).push().updateChildren(restRating);

            /* Update ratings */
            Map<String, Object> riderRating = new HashMap<>();
            riderRating.put("name", userProfile.getName());
            riderRating.put("comment", riderComment.getText().toString());
            riderRating.put("date", dateFormat.format(date));
            riderRating.put("rating", riderRatingBar.getRating());
            databaseReference.child("ratings").child("riders").child(order.getDeliverymanID()).push().updateChildren(riderRating);

            /* Update dishes */
            for (int i = 0; i < dishes.size(); i++) {
                final DatabaseReference ref = databaseReference.child("offers").child(order.getRestaurantID()).child(dishes.get(i).getId());
                final Integer dishRating = dishes.get(i).getRating();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object rating = dataSnapshot.child("rating").getValue();
                        Object count = dataSnapshot.child("count").getValue();
                        ref.child("rating").setValue(Integer.parseInt(rating.toString()) + dishRating);
                        ref.child("count").setValue(Integer.parseInt(count.toString()) + 1);

                        Integer value = Math.round((Integer.parseInt(rating.toString()) + dishRating) / (Integer.parseInt(count.toString()) + 1));
                        try {
                            JSONObject object = new JSONObject()
                                    .put("rating", value);

                            index.partialUpdateObjectAsync(object, order.getRestaurantID(), false, null);
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            /* Update restaurant */
            final DatabaseReference ref = databaseReference.child("restaurants").child(order.getRestaurantID());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object rating = dataSnapshot.child("rating").getValue();
                    Object count = dataSnapshot.child("count").getValue();
                    ref.child("rating").setValue(Integer.parseInt(rating.toString()) + Math.round(ratingBar2.getRating()));
                    ref.child("count").setValue(Integer.parseInt(count.toString()) + 1);
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

            /* Update orders */
            order.setRestaurantComment(comment.getText().toString());
            order.setRestaurantRating(Float.toString(ratingBar2.getRating()));
            order.setRiderComment(riderComment.getText().toString());
            order.setRiderRating(Float.toString(ratingBar2.getRating()));
            order.setCart(dishes);

            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put("restaurantComment", comment.getText().toString());
            updateMap.put("restaurantRating", Float.toString(ratingBar2.getRating()));
            updateMap.put("riderComment", riderComment.getText().toString());
            updateMap.put("riderRating", Float.toString(riderRatingBar.getRating()));
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

            for (int i = 0; i < dishes.size(); i++) {
                databaseReference.child("orders").child(order.getId()).child("cart").child(dishes.get(i).getId()).child("rating").setValue(dishes.get(i).getRating().toString());
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.evaluation_Err), Toast.LENGTH_SHORT);
        }
    }
}
