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
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.notifications.NotificationsFragment;
import com.madness.degustibus.R;
import com.madness.degustibus.order.OrderFragment;

import java.util.ArrayList;

/**
 * The HomeFragment inflates the layout for the homepage of the application.
 */

public class HomeFragment extends Fragment implements HomeDataAdapter.ItemClickListener{

    ArrayList<HomeClass> restaurantList = new ArrayList<>();
    HomeClass rest;
    private RecyclerView recyclerView;
    private HomeDataAdapter mAdapter;
    private DatabaseReference databaseRef;
    private Fragment fragment;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setHomeDataAdapter();
    }

    /* Here is set the Adapter */
    private void setHomeDataAdapter() {
        mAdapter = new HomeDataAdapter(restaurantList,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle(getString(R.string.title_Home));

        recyclerView = rootView.findViewById(R.id.recyclerViewHome);
        mAdapter = new HomeDataAdapter(restaurantList,this
        );
        populateList();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restaurantList = savedInstanceState.getParcelableArrayList("Restaurant");
            setHomeDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* Checks if the fragment actually loaded is the home fragment, in case no disable the saving operation */
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.flContent);
        if( fragment instanceof HomeFragment ) {
            View rootView = getLayoutInflater().inflate(R.layout.fragment_home, (ViewGroup) getView().getParent(), false);
            recyclerView = rootView.findViewById(R.id.recyclerViewHome);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                outState.putParcelableArrayList("Restaurant", new ArrayList<>(mAdapter.getList()));
            }
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

    @Override
    public void onListItemClick(int clickedItemIndex) {
        restaurantList.get(clickedItemIndex);
        try {

            Bundle bundle = new Bundle();
            bundle.putString("restId",restaurantList.get(clickedItemIndex).getId());
            fragment = null;
            fragment = OrderFragment.class.newInstance();
            fragment.setArguments(bundle);

            int i=0;


        } catch (Exception e) {
            Log.e("MAD", "editProfileClick: ", e);
        }

        ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, fragment, " Order")
                .addToBackStack("Home")
                .commit();
    }
    //method to popultate list
    void populateList (){
        databaseRef = FirebaseDatabase.getInstance().getReference("restaurants");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    rest = new HomeClass(dS.getValue(HomeClass.class).getName(),dS.getValue(HomeClass.class).getAddress(),dS.getValue(HomeClass.class).getDesc(),dS.getValue(HomeClass.class).getPic(),dS.getKey());
                    restaurantList.add(rest);
                    recyclerView.setAdapter(mAdapter);
                }
                 /* Here is checked if there are elements to be displayed, in case nothing can be shown an
                 icon is set as visible and the other elements of the fragment are set invisible.
                 */
                if (mAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);

                    LinearLayout linearLayout = getView().findViewById(R.id.emptyLayout);
                    linearLayout.setVisibility(View.VISIBLE);
                    LinearLayout linearLayout1 = getView().findViewById(R.id.fabLayout);
                    linearLayout1.setVisibility(View.INVISIBLE);
                } else {
                    LinearLayout linearLayout = getView().findViewById(R.id.emptyLayout);
                    linearLayout.setVisibility(View.INVISIBLE);
                    LinearLayout linearLayout1 = getView().findViewById(R.id.fabLayout);
                    linearLayout1.setVisibility(View.VISIBLE);

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(manager);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
