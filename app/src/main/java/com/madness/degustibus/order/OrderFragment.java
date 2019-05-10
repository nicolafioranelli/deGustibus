package com.madness.degustibus.order;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.madness.degustibus.GlideApp;
import com.madness.degustibus.R;
import com.madness.degustibus.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderFragment extends Fragment {

    private ArrayList<Dish> dishList;
    private Dish dish;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private Button confirm_btn;
    private Fragment fragment;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private NewOrderInterface newOrderInterface;
    private ValueEventListener emptyListener;
    private String picture;

    public OrderFragment() {
        // Required empty public constructor
    }

    /* The onAttach method registers the newOrderInterface */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewOrderInterface) {
            newOrderInterface = (NewOrderInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListener");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle(getString(R.string.title_Order));

        dishList = new ArrayList<>();
        confirm_btn = rootView.findViewById(R.id.complete_order_btn);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cart = 0;
                // at first clear the cart clear the db
                FirebaseDatabase.getInstance().getReference()
                        .child("customers")
                        .child(user.getUid())
                        .child("cart")
                        .removeValue();

                // store the selected dishes in the cart of the user
                for (Dish dish : dishList) {    // for each dish in the dailyoffer
                    if (dish.quantity > 0) {    // keep only the selected ones

                        // store it on firebase
                        // nb. do not use the attribute `databaceReference`
                        // since it will be modified during the execution of the class
                        // poitnig to orders

                        Map<String, Object> cartItem = new HashMap<>();
                        cartItem.put(dish.identifier, dish);

                        FirebaseDatabase.getInstance().getReference()
                                .child("customers")
                                .child(user.getUid())
                                .child("cart").updateChildren(cartItem);

                        cart++;
                    }
                }

                // if at least one dish is selected call the checkout
                if (cart > 0) {
                    newOrderInterface.goToCart(user.getUid());
                } else {
                    Toast.makeText(getContext(),
                            getString(R.string.select),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        loadFromFirebase(rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dishList = savedInstanceState.getParcelableArrayList("Menu");
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String piatto = "0";
        for (Dish dish : dishList) {
            outState.putString(piatto, dish.avail);
            piatto = String.valueOf(Integer.valueOf(piatto) + 1);
        }
    }

    /* Populates the menu with the notification button */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Add action to be performed once the item on the toolbar is clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_notifications) {
            Fragment fragment = null;
            Class fragmentClass;
            try {
                fragmentClass = NotificationsFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Notifications").addToBackStack("HOME").commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFromFirebase(final View rootView) {
        // catch the id of the restaurant from the bundle
        final String rest = this.getArguments().getString("restId");

        // set the top pic
        FirebaseDatabase.getInstance()
                .getReference()
                .child("restaurants")
                .child(rest)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        picture = objectMap.get("photo").toString();

                        ImageView image = rootView.findViewById(R.id.rest_imageView);
                        GlideApp.with(getContext())
                                .load(picture)
                                .placeholder(R.drawable.restaurant)
                                .into(image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // obtain the url /offers/{restaurantIdentifier}
        Query query = FirebaseDatabase.getInstance().getReference().child("offers").child(rest); // query data at /offers/{restaurantIdentifier}

        FirebaseRecyclerOptions<Dish> options =
                new FirebaseRecyclerOptions.Builder<Dish>()
                        .setQuery(query, new SnapshotParser<Dish>() {
                            @NonNull
                            @Override
                            public Dish parseSnapshot(@NonNull DataSnapshot snapshot) {
                                dish = snapshot.getValue(Dish.class);  // get the snapshot and cast it
                                // into a `Dish` item
                                dishList.add(dish);                         // add the `dish` into the list
                                return dish;                                // return the item to the builder
                            }
                        }).build();                                         // build the option

        // new adapter
        adapter = new FirebaseRecyclerAdapter<Dish, DishHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DishHolder holder, final int position, @NonNull Dish model) {

                final Integer maxAvail = Integer.parseInt(model.getAvail());

                holder.title.setText(model.getDish());
                holder.description.setText(model.type);
                holder.price.setText(model.getPrice() + " €");
                holder.quantity.setText("0");

                GlideApp.with(holder.image.getContext())
                        .load(model.getPic())
                        .placeholder(R.drawable.dish_image)
                        .into(holder.image);

                // set button plus
                holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = Integer.parseInt(holder.quantity.getText().toString());
                        // check for the availability of the product
                        if (n < maxAvail) {
                            n++;
                            holder.quantity.setText(String.valueOf(n));
                            dishList.get(position).setQuantity(n);
                        }

                    }
                });

                // set button minus
                holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = Integer.parseInt(holder.quantity.getText().toString());
                        // check for non negative numbers
                        if (n > 0) {
                            n--;
                            holder.quantity.setText(String.valueOf(n));
                            dishList.get(position).setQuantity(n);
                        }
                    }
                });

            }

            @Override
            public DishHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_listitem, parent, false);

                return new DishHolder(view);
            }
        };

        emptyListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    rootView.findViewById(R.id.menu).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.complete_order_btn).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    rootView.findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.complete_order_btn).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    @Override
    public void onDetach() {
        super.onDetach();
        databaseRef.removeEventListener(emptyListener);
    }

    /* Here is defined the interface for the HomeActivity in order to manage the click */
    public interface NewOrderInterface {
        void goToCart(String identifier);
    }
}