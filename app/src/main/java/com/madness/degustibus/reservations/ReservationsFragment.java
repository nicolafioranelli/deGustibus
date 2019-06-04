package com.madness.degustibus.reservations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    public ReservationsFragment() {
        // Required empty public constructor
    }

    /* Lifecycle */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reservations, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Query query = databaseReference.child("orders").orderByChild("customerID").equalTo(user.getUid());

        FirebaseRecyclerOptions<OrderClass> options =
                new FirebaseRecyclerOptions.Builder<OrderClass>()
                        .setQuery(query, new SnapshotParser<OrderClass>() {
                            @NonNull
                            @Override
                            public OrderClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                List<ItemClass> cart = new ArrayList<>();
                                for (DataSnapshot obj : snapshot.child("cart").getChildren()) {
                                    ItemClass item = new ItemClass(Integer.parseInt(obj.child("quantity").getValue().toString()),
                                            obj.getKey(),
                                            obj.child("name").getValue().toString(),
                                            Integer.parseInt(obj.child("rating").getValue().toString())
                                    );
                                    cart.add(item);
                                }

                                OrderClass order = new OrderClass(snapshot.getKey(),
                                        snapshot.child("customerID").getValue().toString(),
                                        snapshot.child("restaurantID").getValue().toString(),
                                        snapshot.child("deliverymanID").getValue().toString(),
                                        snapshot.child("deliveryDate").getValue().toString(),
                                        snapshot.child("deliveryHour").getValue().toString(),
                                        snapshot.child("totalPrice").getValue().toString(),
                                        snapshot.child("customerAddress").getValue().toString(),
                                        snapshot.child("restaurantAddress").getValue().toString(),
                                        snapshot.child("status").getValue().toString(),
                                        snapshot.child("riderComment").getValue().toString(),
                                        snapshot.child("riderRating").getValue().toString(),
                                        snapshot.child("restaurantComment").getValue().toString(),
                                        snapshot.child("restaurantRating").getValue().toString(),
                                        cart
                                );

                                return order;
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<OrderClass, OrderHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderHolder holder, final int position, @NonNull final OrderClass model) {
                if (model.getDeliverymanID().equals("null")) {
                    holder.deliveryman.setText(R.string.no_rider);
                } else {
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

                holder.date.setText(model.getDeliveryDate());
                holder.hour.setText(model.getDeliveryHour());
                holder.price.setText(model.getTotalPrice() + " €");
                databaseReference.child("restaurants").child(model.getRestaurantID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            String restaurantName = objectMap.get("name").toString();
                            holder.restaurant.setText(restaurantName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (model.getStatus().equals("new")) {
                    holder.status.setText(R.string.status_new);
                } else if (model.getStatus().equals("incoming")) {
                    holder.status.setText(R.string.status_elaboration);
                } else if (model.getStatus().equals("refused")) {
                    holder.status.setText(R.string.status_refused);
                } else if (model.getStatus().equals("done")) {
                    holder.status.setText(R.string.status_done);
                } else if (model.getStatus().equals("elaboration")) {
                    holder.status.setText(R.string.status_elaboration);
                } else if (model.getStatus().equals("delivering")) {
                    holder.status.setText(R.string.status_delivering);
                }

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Fragment fragment = null;
                            fragment = DetailedResFragment.class.newInstance();
                            Bundle args = new Bundle();
                            args.putString("restaurant", holder.restaurant.getText().toString());
                            args.putString("orderID", model.getId());
                            fragment.setArguments(args);

                            ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.flContent, fragment, "Detail")
                                    .addToBackStack("Reservations")
                                    .commit();

                        } catch (Exception e) {
                            Log.e("MAD", "onClick: ", e);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public OrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.order_item, viewGroup, false);
                return new OrderHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
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
