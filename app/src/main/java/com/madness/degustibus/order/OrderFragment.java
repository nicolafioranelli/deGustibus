package com.madness.degustibus.order;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madness.degustibus.R;
import com.madness.degustibus.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;



public class OrderFragment extends Fragment{

    ArrayList<MenuClass> dishList = new ArrayList<>();
    MenuClass dish;
    FirebaseDatabase database;
    DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private Button confirm_btn;
    private Fragment fragment;
    HashMap<String,String> order=new HashMap<>();
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle("New order");

        confirm_btn = rootView.findViewById(R.id.complete_order_btn);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // TODO the new fragment must be called by the main activity

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = null;
                    Class fragmentClass;
                    fragmentClass = CompletedOrderFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    for(MenuClass dish: dishList){

                        if(Integer.parseInt(dish.avail)>0){
                            order.put("dishname",dish.dish);
                            order.put("price",dish.price);
                            order.put("quantity",dish.avail);
                            FirebaseDatabase.getInstance().getReference().child("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart").push().setValue(order);
                        }
                    }
                } catch (Exception e) {
                    Log.e("MAD", "editProfileClick: ", e);
                }

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, fragment, "Complete order")
                        .addToBackStack("Home")
                        .commit();
            }
        });
        loadFromFirebase();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dishList = savedInstanceState.getParcelableArrayList("Menu");
            //setMenuDataAdapter();
            //recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* Checks if the fragment actually loaded is the home fragment, in case no disable the saving operation */
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.flContent);
        if( fragment instanceof OrderFragment) {
            View rootView = getLayoutInflater().inflate(R.layout.fragment_order, (ViewGroup) getView().getParent(), false);
            recyclerView = rootView.findViewById(R.id.recyclerViewNotf);
        }
        String piatto = "0";
        for(MenuClass dish: dishList){
            outState.putString(piatto,dish.avail);
            piatto = String.valueOf(Integer.valueOf(piatto)+1);
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

    void loadFromFirebase(){

        // cacth the id of the restaurant from the boundle
        final String rest = this.getArguments().getString("restId");

        // obtain the url /offers/{restaurantIdentifier}
        databaseRef = FirebaseDatabase.getInstance().getReference().child("offers").child(rest);
        Query query = databaseRef; // query data at /offers/{restaurantIdentifier}

        FirebaseRecyclerOptions<MenuClass> options =
                new FirebaseRecyclerOptions.Builder<MenuClass>()
                .setQuery(query, new SnapshotParser<MenuClass>() {
                    @NonNull
                    @Override
                    public MenuClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                        dish = snapshot.getValue(MenuClass.class);  // get the snapshot and cast it
                                                                    // into a `MenuClass` item
                        dishList.add(dish);                         // add the `dish` into the list
                        return dish;                                // return the item to the builder
                    }
                }).build();                                         // build the option

        // new adapter
        adapter = new FirebaseRecyclerAdapter<MenuClass, MenuHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuHolder holder, final int position, @NonNull MenuClass model) {

                final Integer maxAvail = Integer.parseInt(model.getAvail());

                holder.title.setText(model.getDish());
                holder.description.setText(model.getType());
                holder.price.setText(model.getPrice() + " €");
                holder.quantity.setText("0");

                // TODO do it with Glide
                if (model.getPic() == null) {
                    // Set default image
                    holder.image.setImageResource(R.drawable.dish_image);
                } else {
                    holder.image.setImageURI(Uri.parse(model.getPic()));
                }

                // set button plus
                holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n =Integer.parseInt(holder.quantity.getText().toString());
                        // check for the availability of the product
                        if(n < maxAvail){
                            n ++;
                            holder.quantity.setText(String.valueOf(n));
                            // NB: it represent the selected quantity
                            dish.setAvail(String.valueOf(n));
                        }

                    }
                });

                // set button minus
                holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n =Integer.parseInt(holder.quantity.getText().toString());
                        // check for non negative numbers
                        if(n > 0){
                            n --;
                            holder.quantity.setText(String.valueOf(n));
                            // NB: it represent the selected quantity
                            dish.setAvail(String.valueOf(n));
                        }
                    }
                });

            }

            @Override
            public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_listitem, parent, false);
                return new MenuHolder(view);
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
