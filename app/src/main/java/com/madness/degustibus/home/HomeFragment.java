package com.madness.degustibus.home;

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
import com.madness.degustibus.order.OrderFragment;

import java.util.ArrayList;

/**
 * The HomeFragment inflates the layout for the homepage of the application.
 */

public class HomeFragment extends Fragment {

    ArrayList<HomeClass> restaurantList = new ArrayList<>();
    HomeClass rest;
    private RecyclerView recyclerView;
    private DatabaseReference databaseRef;
    private Fragment fragment;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private android.support.v7.widget.SearchView byName;
    private ValueEventListener emptyListener;
    private FirebaseUser user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle(getString(R.string.title_Home));

        recyclerView = rootView.findViewById(R.id.recyclerViewHome);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            populateList(rootView);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView

        byName = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        byName.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firebaseSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                firebaseSearch(s);
                return false;
            }
        });
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

    /* Populate list */
    private void populateList(final View rootView) {
        databaseRef = FirebaseDatabase.getInstance().getReference("restaurants");
        Query query = FirebaseDatabase.getInstance().getReference().child("restaurants");

        FirebaseRecyclerOptions<HomeClass> options =
                new FirebaseRecyclerOptions.Builder<HomeClass>()
                        .setQuery(query, new SnapshotParser<HomeClass>() {
                            @NonNull
                            @Override
                            public HomeClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return rest = new HomeClass(snapshot.getValue(HomeClass.class).getName(), snapshot.getValue(HomeClass.class).getAddress(), snapshot.getValue(HomeClass.class).getDesc(), snapshot.getValue(HomeClass.class).getPic(), snapshot.getKey());
                            }
                        })
                        .build();
        adapter = new FirebaseRecyclerAdapter<HomeClass, HomeHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeHolder holder, final int position, @NonNull final HomeClass model) {
                //HomeClass restaurant = restaurantList.get(position);
                holder.title.setText(model.getName());
                holder.subtitle.setText(model.getAddress());
                holder.description.setText(model.getDesc());
                GlideApp.with(holder.image.getContext())
                        .load(model.getPic())
                        .placeholder(R.drawable.restaurant)
                        .into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rest_id = getRef(position).getKey();
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("restId", rest_id);
                            bundle.putString("restName", model.getName());
                            bundle.putString("restAddress", model.getAddress());
                            fragment = null;
                            fragment = OrderFragment.class.newInstance();
                            fragment.setArguments(bundle);

                        } catch (Exception e) {
                            Log.e("MAD", "editProfileClick: ", e);
                        }

                        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, fragment, " Order")
                                .addToBackStack("HOME")
                                .commit();
                    }
                });
            }

            @Override
            public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurants_listitem, parent, false);

                HomeHolder hold = new HomeHolder(view);
                return hold;
            }
        };
        recyclerView.setAdapter(adapter);


        /* Listener to check if the recycler view is empty */
        emptyListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    rootView.findViewById(R.id.emptyLayout).setVisibility(View.GONE);
                    rootView.findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    rootView.findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery = databaseRef.orderByChild("name").startAt(searchText);

        FirebaseRecyclerOptions<HomeClass> options =
                new FirebaseRecyclerOptions.Builder<HomeClass>()
                        .setQuery(firebaseSearchQuery, new SnapshotParser<HomeClass>() {
                            @NonNull
                            @Override
                            public HomeClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                rest = new HomeClass(snapshot.getValue(HomeClass.class).getName(), snapshot.getValue(HomeClass.class).getAddress(), snapshot.getValue(HomeClass.class).getDesc(), snapshot.getValue(HomeClass.class).getPic(), snapshot.getKey());
                                System.out.println("Ristorante " + rest.name);
                                return rest;
                            }
                        })
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<HomeClass, HomeHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeHolder holder, final int position, @NonNull final HomeClass model) {
                final String restName = model.getName();
                holder.title.setText(restName);
                final String restAddress = model.getAddress();
                holder.subtitle.setText(restAddress);
                holder.description.setText(model.getDesc());
                //holder.image.setImageResource(R.drawable.restaurant);

                GlideApp.with(holder.image.getContext())
                        .load(model.getPic())
                        .placeholder(R.drawable.restaurant)
                        .into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rest_id = getRef(position).getKey();
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("restId", rest_id);
                            bundle.putString("restName", restName);
                            bundle.putString("restAddress", restAddress);
                            fragment = null;
                            fragment = OrderFragment.class.newInstance();
                            fragment.setArguments(bundle);

                        } catch (Exception e) {
                            Log.e("MAD", "editProfileClick: ", e);
                        }

                        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, fragment, " Order")
                                .addToBackStack("HOME")
                                .commit();
                    }
                });
            }

            @Override
            public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurants_listitem, parent, false);

                HomeHolder hold = new HomeHolder(view);
                return hold;

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (user != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (user != null) {
            databaseRef.removeEventListener(emptyListener);
        }
    }
}
