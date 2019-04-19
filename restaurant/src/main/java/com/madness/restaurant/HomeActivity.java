package com.madness.restaurant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.madness.restaurant.daily.DailyFragment;
import com.madness.restaurant.daily.NewDailyOffer;
import com.madness.restaurant.home.HomeFragment;
import com.madness.restaurant.profile.EditProfile;
import com.madness.restaurant.profile.ProfileFragment;
import com.madness.restaurant.reservations.NewReservationFragment;
import com.madness.restaurant.reservations.ReservationFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,
        ProfileFragment.ProfileListener, ReservationFragment.ReservationListener, DailyFragment.DailyListener,
NewReservationFragment.NewReservationListener, NewDailyOffer.NewDailyOfferListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbarhome);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Instantiate home fragment */
        if(savedInstanceState == null) {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = HomeFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                navigationView.getMenu().getItem(0).setChecked(true);
            } catch (Exception e) {
                Log.e("MAD", "onCreate: ", e);
            }
        } else {
            fragment = (Fragment) getSupportFragmentManager().findFragmentByTag("HOME");
        }
    }

    @Override
    public void onItemClicked() {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = EditProfile.class;
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e("MAD", "onItemClicked: ", e);
            }

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.flContent, fragment, "EditP");
            ft.addToBackStack("PROFILE");
            ft.commit();
    }

    @Override
    public void addReservation() {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = NewReservationFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e("MAD", "onItemClicked: ", e);
            }

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.flContent, fragment, "AddReservation");
            ft.addToBackStack("RESERVATION");
            ft.commit();
    }

    @Override
    public void addDailyOffer() {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = NewDailyOffer.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "onItemClicked: ", e);
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "AddOffer");
        ft.addToBackStack("DAILY");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /* Inflate the menu: this adds items to the action bar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /* Handle clicks on action toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        fragment = null;
        Class fragmentClass;
        // if EditProfile fragment is changed onNavigationItemSelected start a popBackStack
        EditProfile editFrag = (EditProfile)
                getSupportFragmentManager().findFragmentByTag("EditP");
        NewDailyOffer newDailyOffer = (NewDailyOffer)
                getSupportFragmentManager().findFragmentByTag("AddOffer");
        NewReservationFragment newReservationFragment = (NewReservationFragment)
                getSupportFragmentManager().findFragmentByTag("AddReservation");

        /* The switch now contains also the string helpful to identify the fragment in the fragment stack */
        switch(item.getItemId()) {
            case R.id.nav_profile:
                try{
                    if (editFrag != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newDailyOffer != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newReservationFragment != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    fragmentClass = ProfileFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    // Insert the fragment by replacing any existing fragment
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "PROFILE").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_reservations:
                try{
                    if (editFrag != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newDailyOffer != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newReservationFragment != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                        fragmentClass = ReservationFragment.class;
                        fragment = (Fragment) fragmentClass.newInstance();
                        // Insert the fragment by replacing any existing fragment
                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "RESERVATION").commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_daily:
                try {
                    if (editFrag != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newDailyOffer != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newReservationFragment != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    fragmentClass = DailyFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    // Insert the fragment by replacing any existing fragment
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "DAILY").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try{
                    if (editFrag != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newDailyOffer != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    if (newReservationFragment != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    // Insert the fragment by replacing any existing fragment
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } // end switch

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        //setTitle(item.getTitle());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        EditProfile editFrag = (EditProfile)
                getSupportFragmentManager().findFragmentByTag("EditP");
        NewReservationFragment editRes = (NewReservationFragment)
                getSupportFragmentManager().findFragmentByTag("AddReservation");
        if (editFrag != null) {
            editFrag.setHourAndMinute(hourOfDay,minute);
        }
        else if(editRes != null) {
            editRes.setHourAndMinute(hourOfDay,minute);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        NewReservationFragment editRes = (NewReservationFragment)
                getSupportFragmentManager().findFragmentByTag("AddReservation");
         if(editRes != null) {
            editRes.setDate(year, month, dayOfMonth);
        }
    }

    @Override
    public void onSubmit() {
        ReservationFragment reservationFragment = (ReservationFragment)
                getSupportFragmentManager().findFragmentByTag("RESERVATION");
        if(reservationFragment != null) {
            reservationFragment.addOnReservation();
        }
    }
    @Override
    public void onSubmitDish() {
        DailyFragment dailyFragment = (DailyFragment)
                getSupportFragmentManager().findFragmentByTag("DAILY");
        if(dailyFragment != null) {
            dailyFragment.addOnDaily();
        }
    }
}
