package com.madness.degustibus;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.auth.LoginActivity;
import com.madness.degustibus.home.HomeFragment;
import com.madness.degustibus.notifications.NotificationsFragment;
import com.madness.degustibus.order.CompletedOrderFragment;
import com.madness.degustibus.order.OrderFragment;
import com.madness.degustibus.profile.EditProfileFragment;
import com.madness.degustibus.profile.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class of the main Activity (launch) for the app. In particular the onCreate method checks
 * if a user is already authenticated and in case no one is found, it redirects to the Login Activity
 * to let the user authenticates itself.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileListener,
        OrderFragment.NewOrderInterface,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    Fragment fragment;
    FragmentManager fragmentManager;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private final String CHANNEL_ID = "channelApp";
    private final int NOTIFICATION_ID = 001;

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

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            final TextView userName = navigationView.getHeaderView(0).findViewById(R.id.nameNav);
            databaseReference.child("customers").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if(objectMap.get("name") != null)   // TODO fix, it is always null
                            userName.setText(objectMap.get("name").toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

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

        createNotificationChannel();

        // listen for notifications
        if(user != null) FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            makeNotification(getString(R.string.new_notification), getString(R.string.notification_message));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = CHANNEL_ID;
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void makeNotification(String type, String description){

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // it avoid to recreate the activiy
                                                                                            // it simply call the `onNewIntent()` method
        intent.putExtra("notification", "open");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // show a new notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_toolbar_notifications);
            builder.setContentTitle(type);
            builder.setContentText(description);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String extras = intent.getStringExtra("notification");
        if (extras != null && extras.equals("open")) {
            fragment =  new NotificationsFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").addToBackStack("HOME").commit();
        }
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

        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_connection), Toast.LENGTH_LONG).show();
        }

        if(user!=null) {
            FirebaseDatabase.getInstance().getReference().child("customers").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.errProfile), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        ft.addToBackStack("COMPLETEOFFER"); // TODO change it
        ft.commit();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        CompletedOrderFragment fragment = (CompletedOrderFragment)
                getSupportFragmentManager().findFragmentByTag("Complete offer");
        if (fragment != null) {
            fragment.setDeliveryDate(year, month, dayOfMonth);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        CompletedOrderFragment fragment = (CompletedOrderFragment)
                getSupportFragmentManager().findFragmentByTag("Complete offer");
        if (fragment != null) {
            fragment.setDeliveryTime(hourOfDay, minute);
        }
    }

    /* Check if connection is enabled! */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
