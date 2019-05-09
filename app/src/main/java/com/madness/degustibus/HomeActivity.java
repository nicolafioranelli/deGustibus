package com.madness.degustibus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.madness.degustibus.auth.LoginActivity;
import com.madness.degustibus.home.HomeFragment;
import com.madness.degustibus.order.CompletedOrderFragment;
import com.madness.degustibus.order.OrderFragment;

import java.util.ArrayList;

/**
 * This is the class of the main Activity (launch) for the app. In particular the onCreate method checks
 * if a user is already authenticated and in case no one is found, it redirects to the Login Activity
 * to let the user authenticates itself.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileListener,
        OrderFragment.NewOrderInterface {

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    Fragment fragment;
    FragmentManager fragmentManager;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbarhome);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        /* Check if there is an user authenticated, in case no user launch the login screen */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        /* Instantiate home fragment */
        if (savedInstanceState == null) {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = HomeFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                navigationView.getMenu().getItem(0).setChecked(true);
            } catch (Exception e) {
                Log.e("MAD", "onCreate: ", e);
            }
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag("HOME");
        }

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateMenu();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fragment = null;
        Class fragmentClass;

        switch (item.getItemId()) {
            case R.id.nav_home:
                try {
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").addToBackStack("HOME").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_profile:
                try {
                    fragmentClass = ProfileFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Profile").addToBackStack("HOME").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_settings:
                try {
                    fragmentClass = SettingsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Settings").addToBackStack("HOME").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        item.setChecked(true);

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void editProfileClick() {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = EditProfileFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "editProfileClick: ", e);
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "EditProfile");
        ft.addToBackStack("PROFILE");
        ft.commit();
    }

    private void updateMenu() {
        fragment = fragmentManager.findFragmentById(R.id.flContent);
        if (fragment != null) {
            if (fragment instanceof ProfileFragment) {
                navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            } else if (fragment instanceof EditProfileFragment) {
                navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            } else if (fragment instanceof SettingsFragment) {
                navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
            } else if (fragment instanceof OrderFragment) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else if (fragment instanceof CompletedOrderFragment) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            }
        }
    }

    @Override
    public void goToCart(String userIdentifier) {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = CompletedOrderFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "onItemClicked: ", e);
        }

        Bundle args = new Bundle();
        args.putString("id", userIdentifier);
        fragment.setArguments(args);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "Complete offer");
        //ft.addToBackStack("DAILY"); // TODO change it
        ft.commit();
    }
}
