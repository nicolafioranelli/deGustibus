package com.madness.deliveryman;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.notifications.NotificationsFragment;

import java.security.spec.ECField;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private Boolean mLocationPermissionGaranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 13;

    private MapView mapView;
    private TextView mileage;
    private GoogleMap googleMap;
    private FirebaseUser user;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("deGustibus");

        mileage = rootView.findViewById(R.id.tv_home_km);
        mapView = rootView.findViewById(R.id.home_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        getLocationPermission();
       /* //Set up a callback object that will be activated when the instance of GoogleMap is ready to be used
        mapView.getMapAsync(this);*/

        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            final TextView userName = rootView.findViewById(R.id.welcomeName);
            databaseReference.child("riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    try {
                        if (objectMap.get("name") != null) {
                            userName.setText(objectMap.get("name").toString());
                            mileage.setText(objectMap.get("mileage").toString());
                            rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                            rootView.findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                            rootView.findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return rootView;
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
    //Manipulates the map once available. This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //check for fine and coarse location permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //get the current location of the device
        getDeviceLocation();
        //Initialize Google Play Services
        googleMap.setMyLocationEnabled(true);


    }
    //getting the device current location
    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        try{
            if(mLocationPermissionGaranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location)task.getResult();
                            if(currentLocation!=null){
                                moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                            }

                        }
                    }
                });
            }

        }catch (SecurityException e){

        }
    }
    //runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGaranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{

                //something is garanted
                if(grantResults.length > 0) {
                    //something is not garanted
                    for(int i = 0; i<grantResults.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGaranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGaranted = true;
                    //initialize map
                    mapView.getMapAsync(this);
                }
            }
        }
    }
    // checking location permissions for display the current position on map
    private void getLocationPermission() {
        //list of permission to check
        String[] permission = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //if fine and coarse location permissions are granted set this flag for future checks, else no
                mLocationPermissionGaranted = true;
                System.out.println("QUIIIIIIIIIIIII" + mapView + " EEEE" + this);
                mapView.getMapAsync(this);
            } else {
                ActivityCompat.requestPermissions(this.getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    //moving the camera to latLng with zoom
    private void moveCamera(LatLng latLng,float zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}
