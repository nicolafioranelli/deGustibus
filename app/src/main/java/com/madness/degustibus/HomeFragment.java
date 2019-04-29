package com.madness.degustibus;

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
        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        /*recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restaurantList = savedInstanceState.getParcelableArrayList("Dailies");
            setHomeDataAdapter();
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("Restaurant", new ArrayList<>(mAdapter.getList()));
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
