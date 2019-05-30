package com.madness.deliveryman.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.R;
import com.madness.deliveryman.incoming.IncomingData;
import com.madness.deliveryman.map.FetchUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback, FetchUrl.AsyncFetchResponse {

    private Boolean mLocationPermissionGaranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 13;

    private MapView mapView;
    private TextView streetAddress;
    private TextView routeLenght;
    private TextView routeTime;
    private GoogleMap googleMap;
    private String locationAddress;
    private String name;
    private DatabaseReference databaseReference;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser user;
    private IncomingData incomingData;
    private Address address;
    LatLng currentLocation;
    FetchUrl fetchUrl = new FetchUrl();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this to set delegate/listener back to this class
        fetchUrl.delegate = this;
        //check permission
        getLocationPermission();
        //get user
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get location and name of destination
        locationAddress = getArguments().getString("address");
        name = getArguments().getString("name");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        streetAddress = view.findViewById(R.id.tv_map_address);
        routeLenght = view.findViewById(R.id.tv_map_km);
        routeTime = view.findViewById(R.id.tv_map_time);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        //Set up a callback object that will be activated when the instance of GoogleMap is ready to be used
        mapView.getMapAsync(this);
    }

    //Manipulates the map once available. This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        streetAddress.setText(locationAddress);
        //check for fine and coarse location permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //Initialize Google Play Services
        googleMap.setMyLocationEnabled(true);
        //get the current location of the device
        getDeviceLocation(googleMap);

    }

    //translates the destination address and makes the url request
    private void mapOperations (){

        //find the address
        address = geoLocate(locationAddress);
        //converte Location into LatLong
        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
        //adding the restaurant marker into the map
        googleMap.addMarker(new MarkerOptions()
                .position(destination)
                .title(name));
        //set position and zoom of the camera
        moveCamera(currentLocation, DEFAULT_ZOOM);
        //create a URL to make a request to find the path from current location to destination location
        String url = getDirectionsUrl(currentLocation, destination);
        fetchUrl.setMap(googleMap);
        fetchUrl.execute(url);
    }

    // update the view when FetchUrl ends adding distance and duration of the road
    @Override
    public void processFetchFinish(String distance, String duration) {
        routeLenght.setText(distance);
        routeTime.setText(duration);
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
            } else {
                ActivityCompat.requestPermissions(this.getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //getting the device current location
    private void getDeviceLocation(final GoogleMap Gmap) {
        //get current position from firebase
        GeoFire geoFire = new GeoFire(FirebaseDatabase
                .getInstance()
                .getReference()
                .child("positions"));
        //get current location of device
        geoFire.getLocation(user.getUid(), new LocationCallback() {
            //when finds the position, update the map
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                currentLocation = new LatLng(location.latitude,location.longitude);
                mapOperations();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //moving the camera to latLng with zoom
    private void moveCamera(LatLng latLng,float zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
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

    // process of transforming a street address or other description of a location into a (latitude, longitude) coordinate (address)
    private Address geoLocate(String addressName){
        Geocoder geocoder = new Geocoder(this.getContext());
        List<Address> list = new ArrayList<>();
        try{
            //Returns an array of Addresses that are known to describe the named location
            list = geocoder.getFromLocationName(addressName,1);
        }catch (IOException e){

        }

        if(list.size() > 0){
            //return the first result
            return list.get(0);
        }
        else return null;

    }

    //create URL request
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        //set mode
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }

}
