package com.madness.deliveryman;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.riderReviews.RiderReviewsFragment;
import com.madness.deliveryman.auth.LoginActivity;
import com.madness.deliveryman.incoming.IncomingFragment;
import com.madness.deliveryman.location.Tracker;
import com.madness.deliveryman.notifications.NotificationsFragment;
import com.madness.deliveryman.profile.EditProfileFragment;
import com.madness.deliveryman.profile.ProfileFragment;
import com.madness.deliveryman.map.MapFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileListener, OnMapReadyCallback, IncomingFragment.Maps {

    private static final int REQUEST_PERMISSIONS = 100;
    private final String CHANNEL_ID = "channelDeliveryMan";
    private final int NOTIFICATION_ID = 001;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private boolean gps_permission;
    private Tracker tracker;
    private LocationCallback locationCallback;
    private boolean avail;
    private Switch available;
    private DatabaseReference databaseReference;
    private DatabaseReference listenerReference;
    private ValueEventListener listener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbarhome);
        setSupportActionBar(toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        available = new Switch(this);

        firebaseAuth = FirebaseAuth.getInstance();

        /* Check if there is an user authenticated, in case no user launch the login screen */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish(); // it terminate the activity
                }
            }
        };

        fragmentManager = getSupportFragmentManager();
        if (getIntent().hasExtra("newCreation")) {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = EditProfileFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();

                Bundle args = new Bundle();
                args.putBoolean("isNew", true);
                fragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
                navigationView.getMenu().getItem(1).setChecked(true);
            } catch (Exception e) {
                Log.e("MAD", "onCreate: ", e);
            }
        } else {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = HomeFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
            } catch (Exception e) {
                Log.e("MAD", "onCreate: ", e);
            }
        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final TextView userName = navigationView.getHeaderView(0).findViewById(R.id.nameNav);
        try {
            listenerReference = databaseReference.child("riders").child(user.getUid());
            listener = listenerReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    try {
                        if (objectMap.get("name") != null) {
                            userName.setText(objectMap.get("name").toString());
                            avail = (boolean) objectMap.get("available");
                            if (avail) {
                                available.setChecked(true);
                            } else {
                                available.setChecked(false);
                            }
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.e("MAD", "onCreate: ", e);
        }

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateMenu();
            }
        });

        if (user != null) {
            // it manages the use position
            tracker = new Tracker(this, user.getUid(), locationCallback);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(getApplicationContext(), "Please allow the GPS", Toast.LENGTH_LONG).show();
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS);

                    // REQUEST_PERMISSIONS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                tracker.storeTheFirstPosition();
            }
        }


        // register a listener on the switch button in the navigation drawer
        navigationView.getMenu().findItem(R.id.nav_available).setActionView(available);
        available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Map<String, Object> m = new HashMap<String, Object>();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // update the database
                m.put("available", isChecked);
                FirebaseDatabase.getInstance().getReference()
                        .child("riders")
                        .child(user.getUid())
                        .updateChildren(m);
            }
        });

        if (user != null) {
            // at the beginning the user is not available
            available.setChecked(false);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("available", false);
            FirebaseDatabase.getInstance().getReference()
                    .child("riders")
                    .child(user.getUid())
                    .updateChildren(m);

        }

        createNotificationChannel();

        // listen for notifications
        if (user != null) FirebaseDatabase.getInstance().getReference()
                .child("notifications")
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
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

    private void makeNotification(String type, String description) {

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
            fragment = new NotificationsFragment();
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
        try {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("available",false);
            FirebaseDatabase.getInstance().getReference()
                    .child("riders")
                    .child(user.getUid())
                    .updateChildren(m);
        } catch (Exception e) {

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

    @Override
    protected void onPause() {
        super.onPause();
        // stop tracking
        if (tracker != null && tracker.isStartUpdates())
            tracker.stopLocationUpdates();
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
            case R.id.nav_incoming:
                try {
                    fragmentClass = IncomingFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Incoming").addToBackStack("HOME").commit();
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

    @Override
    public void reviewsClick() {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = RiderReviewsFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "reviewsClick: ", e);
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "RestarantReviews");
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
            } else if (fragment instanceof IncomingFragment) {
                navigationView.getMenu().findItem(R.id.nav_incoming).setChecked(true);
            } else if (fragment instanceof NotificationsFragment) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else if (fragment instanceof SettingsFragment) {
                navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
            } else{
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            }
        }

        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_connection), Toast.LENGTH_LONG).show();
        }

        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracker.storeTheFirstPosition();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.gps), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            listenerReference.removeEventListener(listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // resume the tracking
        if (tracker != null && tracker.isStartUpdates()) {
            tracker.startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //do nothing
    }

    @Override
    public void callMaps(String name, String address) {
        try {

            Bundle args = new Bundle();
            args.putString("name", name);
            args.putString("address", address);
            Fragment fragment = new MapFragment();
            fragment.setArguments(args);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Maps").addToBackStack("INCOMING").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
