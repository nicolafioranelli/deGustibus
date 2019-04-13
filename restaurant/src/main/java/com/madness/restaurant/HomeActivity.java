package com.madness.restaurant;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileListener {

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        if (fragment != null ) {
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
            ft.replace(R.id.flContent, fragment);
            ft.addToBackStack("PROFILE");
            ft.commit();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        fragment = null;
        Class fragmentClass;

        /* The switch now contains also the string helpful to identify the fragment in the fragment stack */
        switch(item.getItemId()) {
            case R.id.nav_profile:
                try{
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
                try{
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
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    // Insert the fragment by replacing any existing fragment
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
