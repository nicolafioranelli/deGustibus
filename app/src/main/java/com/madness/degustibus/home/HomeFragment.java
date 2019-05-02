package com.madness.degustibus.home;

import android.os.Bundle;
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
import android.widget.LinearLayout;

import com.madness.degustibus.notifications.NotificationsFragment;
import com.madness.degustibus.R;

import java.util.ArrayList;

/**
 * The HomeFragment inflates the layout for the homepage of the application.
 */

public class HomeFragment extends Fragment {

    ArrayList<HomeClass> restaurantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HomeDataAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
        fakeConstructor();
    }

    /* Here is set the content to be shown, this method will be removed from the following lab */
    private void fakeConstructor() {
        HomeClass daily = new HomeClass("Pizza Express", "Via Montebello, 3", "Specializzati in pizza fritta", null);
        this.restaurantList.add(daily);

        HomeClass daily1 = new HomeClass("Pizza Express", "Via Montebello, 3", "Specializzati in pizza fritta", null);
        this.restaurantList.add(daily1);

        HomeClass daily2 = new HomeClass("Pizza Express", "Via Montebello, 3", "Specializzati in pizza fritta", null);
        this.restaurantList.add(daily2);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setHomeDataAdapter();
    }

    /* Here is set the Adapter */
    private void setHomeDataAdapter() {
        mAdapter = new HomeDataAdapter(restaurantList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle(getString(R.string.title_Home));

        recyclerView = rootView.findViewById(R.id.recyclerView);
        mAdapter = new HomeDataAdapter(restaurantList);

        /* Here is checked if there are elements to be displayed, in case nothing can be shown an
        icon is set as visible and the other elements of the fragment are set invisible.
         */
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
            recyclerView = rootView.findViewById(R.id.recyclerView);
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
}
