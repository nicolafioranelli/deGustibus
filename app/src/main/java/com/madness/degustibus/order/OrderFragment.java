package com.madness.degustibus.order;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.SnapshotHolder;
import com.madness.degustibus.R;
import com.madness.degustibus.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment{

    ArrayList<MenuClass> dishList = new ArrayList<>();
    MenuClass dish;
    DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private MenuDataAdapter mAdapter;
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
        recyclerView = rootView.findViewById(R.id.recyclerViewNotf);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new MenuDataAdapter(dishList);

        /* Here is checked if there are elements to be displayed, in case nothing can be shown an
        icon is set as visible and the other elements of the fragment are set invisible.

        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);

            LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
            linearLayout.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.fabLayout);
            linearLayout1.setVisibility(View.INVISIBLE);
        } else {
            LinearLayout linearLayout = rootView.findViewById(R.id.emptyLayout);
            linearLayout.setVisibility(View.INVISIBLE);
            LinearLayout linearLayout1 = rootView.findViewById(R.id.fabLayout);
            linearLayout1.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(mAdapter);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(manager);
        }
        */

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fragment = null;
                    Class fragmentClass;
                    fragmentClass = CompletedOrderFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    for(MenuClass dish: mAdapter.getList()){

                        if(dish.avail != "0"){
                            order.put("dishname",dish.dish);
                            order.put("price",dish.price);
                            order.put("quantity",dish.avail);
                            databaseRef.child("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart").push().setValue(order);
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
        populateList();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuDataAdapter();
    }

    /* Here is set the Adapter */
    private void setMenuDataAdapter() {
        mAdapter = new MenuDataAdapter(dishList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dishList = savedInstanceState.getParcelableArrayList("Menu");
            setMenuDataAdapter();
            recyclerView.setAdapter(mAdapter);
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
            if (recyclerView.getVisibility() == View.VISIBLE) {
                outState.putParcelableArrayList("Menu", new ArrayList<>(mAdapter.getList()));
            }
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
    void populateList(){
        //Id of resturant clicked
        final String rest = this.getArguments().getString("restId");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("offers");
        Query query = database.getReference().child("offers");

       /* FirebaseRecyclerOptions<MenuClass> options =
                new FirebaseRecyclerOptions.Builder<MenuClass>()
                .setQuery(query, new SnapshotParser<MenuClass>(){
                    @NonNull
                    @Override
                    public MenuClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dS : dataSnapshot.getChildren()){
                            if(dS.getKey().equals(rest)){

                                for(DataSnapshot d: dS.getChildren()){
                                    dish = d.getValue(MenuClass.class);
                                    dishList.add(new MenuClass(dish.getDish(), dish.getType(), dish.getAvail(), dish.getPrice(), dish.getPic()));
                                    recyclerView.setAdapter(mAdapter);
                                }
                            }
                        }
                })
                            .build();*/
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    if(dS.getKey().equals(rest)){

                        for(DataSnapshot d: dS.getChildren()){
                            dish = d.getValue(MenuClass.class);
                            dishList.add(new MenuClass(dish.getDish(), dish.getType(), dish.getAvail(), dish.getPrice(), dish.getPic()));
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
