package com.madness.degustibus;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.madness.degustibus.home.RestaurantDetailsFragment;
import com.madness.degustibus.notifications.NotificationsFragment;
import com.madness.degustibus.order.OrderFragment;
import com.madness.degustibus.profile.EditProfileFragment;
import com.madness.degustibus.profile.ProfileFragment;
import com.madness.degustibus.reservations.DetailedResFragment;
import com.madness.degustibus.reservations.ReservationsFragment;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is the class of the main Activity (launch) for the app. In particular the onCreate method checks
 * if a user is already authenticated and in case no one is found, it redirects to the Login Activity
 * to let the user authenticates itself.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.ProfileListener,
        HomeFragment.HomeInterface,
        RestaurantDetailsFragment.DetailsInterface,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    /* Notifications */
    private final String CHANNEL_ID = "channelApp";
    private final int NOTIFICATION_ID = 001;

    /* Widgets */
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView userName;
    private TextView userEmail;
    private CircleImageView userPhoto;

    /* Fragments */
    private Fragment fragment;
    private FragmentManager fragmentManager;

    /* Firebase */
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference listenerReference;

    /* Listeners */
    private ValueEventListener listener;
    private HashMap<String, Object> userData;

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

    /* Lifecycle */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbarhome);
        setSupportActionBar(toolbar);

        // Retrieve instances of Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        fragmentManager = getSupportFragmentManager();

        /* Check if there is an user authenticated, in case no user launch the login screen */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
                    userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
                    userPhoto = navigationView.getHeaderView(0).findViewById(R.id.userImage);
                    manageNewUser();
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


        // Update highlights in the navigation menu
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateMenu();
            }
        });
        createNotificationChannel();

        // Listen for notifications
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

    private void manageNewUser() {
        fragment = null;

        listenerReference = databaseReference.child("customers").child(user.getUid());
        listener = listenerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    // User created and populated, so retrieve data
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    userName.setText(dataSnapshot.child("name").getValue(String.class));
                    userEmail.setText(dataSnapshot.child("email").getValue(String.class));
                    String photo = null;
                    if (dataSnapshot.hasChild("photo")) {
                        photo = dataSnapshot.child("photo").getValue(String.class);
                    }

                    /* Glide */
                    GlideApp.with(getApplicationContext())
                            .load(photo)
                            .placeholder(R.drawable.user_profile)
                            .into(userPhoto);

                    if (fragmentManager.findFragmentById(R.id.flContent) == null) {
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
                } else {
                    // User created but not populated => go to edit profile
                    if (fragmentManager.findFragmentById(R.id.flContent) == null) {
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        try {
                            fragment = null;
                            Class fragmentClass;
                            fragmentClass = EditProfileFragment.class;
                            fragment = (Fragment) fragmentClass.newInstance();

                            Bundle args = new Bundle();
                            args.putBoolean("isNew", true);
                            fragment.setArguments(args);

                            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "EditProfile").commit();
                            navigationView.getMenu().getItem(1).setChecked(true);
                        } catch (Exception e) {
                            Log.e("MAD", "onCreate: ", e);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    // End lifecycle

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            listenerReference.removeEventListener(listener);
        } catch (Exception e) {

        }
    }

    /* Notification helpers */
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
    // end notification helpers

    private void makeNotification(String type, String description) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // it avoid to recreate the activity
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

    /* Helpers */
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
        String fragmentTag = null;
        fragment = null;
        fragment = fragmentManager.findFragmentById(R.id.flContent);

        if (fragment instanceof ProfileFragment) {
            fragmentTag = "Profile";
        } else if (fragment instanceof EditProfileFragment) {
            fragmentTag = "Edit";
        } else if (fragment instanceof ReservationsFragment) {
            fragmentTag = "Reservation";
        } else if (fragment instanceof DetailedResFragment) {
            fragmentTag = "Detail";
        } else if (fragment instanceof OrderFragment) {
            fragmentTag = "Order";
        } else if (fragment instanceof RestaurantDetailsFragment) {
            fragmentTag = "Restaurant";
        } else if (fragment instanceof NotificationsFragment) {
            fragmentTag = "Notifications";
        } else if (fragment instanceof SettingsFragment) {
            fragmentTag = "Settings";
        }

        fragment = null;
        Class fragmentClass;

        switch (item.getItemId()) {
            case R.id.nav_home:
                try {
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").addToBackStack(fragmentTag).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_profile:
                try {
                    fragmentClass = ProfileFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Profile").addToBackStack(fragmentTag).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_settings:
                try {
                    fragmentClass = SettingsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Settings").addToBackStack(fragmentTag).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_reservations:
                try {
                    fragmentClass = ReservationsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Reservations").addToBackStack(fragmentTag).commit();
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
            } else if (fragment instanceof DetailedResFragment) {
                navigationView.getMenu().findItem(R.id.nav_reservations).setChecked(true);
            } else if (fragment instanceof ReservationsFragment) {
                navigationView.getMenu().findItem(R.id.nav_reservations).setChecked(true);
            } else {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            }
        }

        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_connection), Toast.LENGTH_LONG).show();
        }

        if (user != null) {
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
    // end Helpers

    /* Methods for interfaces */
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

    /* Interface method to go to restaurant details */
    @Override
    public void viewRestaurantDetails(String restaurant, String name) {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = RestaurantDetailsFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "onItemClicked: ", e);
        }

        Bundle args = new Bundle();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Restaurants", Context.MODE_PRIVATE);
        if (sharedPreferences.contains(name)) {
            args.putBoolean("isPreferred", true);
        } else {
            args.putBoolean("isPreferred", false);
        }
        args.putString("restaurant", restaurant);
        fragment.setArguments(args);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "Details");
        ft.addToBackStack("HOME");
        ft.commit();
    }

    /* Interface method to go to new order */
    @Override
    public void newRestaurantOrder(String restaurant) {
        try {
            fragment = null;
            Class fragmentClass;
            fragmentClass = com.madness.degustibus.order.OrderFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("MAD", "onItemClicked: ", e);
        }

        Bundle args = new Bundle();
        args.putString("restaurant", restaurant);
        fragment.setArguments(args);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.flContent, fragment, "Order");
        ft.addToBackStack("Details");
        ft.commit();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        com.madness.degustibus.order.OrderFragment fragment = (com.madness.degustibus.order.OrderFragment)
                getSupportFragmentManager().findFragmentByTag("Order");
        if (fragment != null) {
            fragment.setDeliveryDate(year, month, dayOfMonth);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        com.madness.degustibus.order.OrderFragment fragment = (com.madness.degustibus.order.OrderFragment)
                getSupportFragmentManager().findFragmentByTag("Order");
        if (fragment != null) {
            fragment.setDeliveryTime(hourOfDay, minute);
        }
    }
}
