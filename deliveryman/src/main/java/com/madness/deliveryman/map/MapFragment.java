package com.madness.deliveryman.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class MapFragment extends Fragment implements OnMapReadyCallback  {

    private Boolean mLocationPermissionGaranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private MapView mapView;
    private GoogleMap googleMap;
    private String locationAddress;
    private String name;
    private DatabaseReference databaseReference;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser user;
    private IncomingData incomingData;
    private Address address;
    LatLng currentLocation;
    ArrayList<LatLng> MarkerPoints;


    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check permission
        getLocationPermission();
        //get user
        user = FirebaseAuth.getInstance().getCurrentUser();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        LoadFromFirebase();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Manipulates the map once available. This callback is triggered when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //check for permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //Initialize Google Play Services
        googleMap.setMyLocationEnabled(true);
        getDeviceLocation(googleMap);

    }
    private void mapOperations (){
        //if there is a order
        /*if (incomingData != null ) {
            //take customer address and restaurant id
            customerAddress = incomingData.getCustomerAddress();
            String restaurantID = incomingData.getRestaurantID();

            if (incomingData.getStatus().equals("elaboration")) {
                //get resturant address from resturant ID by firebase
                Query query = databaseReference.child("restaurants/" + restaurantID + "/address");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        restaurantAddress = (String) dataSnapshot.getValue();
                        //find Location of restaurant Address
                        address = geoLocate(restaurantAddress);
                        //converte Location into LatLong
                        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
                        //adding the restaurant marker into the map
                        googleMap.addMarker(new MarkerOptions()
                                .position(destination)
                                .title("Restaurant"));
                        //set position and zoom of the camera
                        moveCamera(currentLocation, 15);
                        //create a URL to make a request to find the path
                        String url = getDirectionsUrl(currentLocation, destination);
                        FetchUrl fetchUrl = new FetchUrl();
                        fetchUrl.setMap(googleMap);
                        fetchUrl.execute(url);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else if (incomingData.getStatus().equals("delivering")) {
                //find Location of customer address
                address = geoLocate(customerAddress);
                //converte Location into LatLong
                LatLng customer = new LatLng(address.getLatitude(), address.getLongitude());
                //adding the restaurant marker into the map
                googleMap.addMarker(new MarkerOptions()
                        .position(customer)
                        .title("Customer"));
                //set position  and zoom of the camera
                moveCamera(currentLocation, 15);
                //create a URL to make a request to find the path
                String url = getDirectionsUrl(currentLocation, customer);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.setMap(googleMap);
                fetchUrl.execute(url);
            }

        }*/

        locationAddress = getArguments().getString("address");
        name = getArguments().getString("name");
        //find the address
        address = geoLocate(locationAddress);
        //converte Location into LatLong
        LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
        //adding the restaurant marker into the map
        googleMap.addMarker(new MarkerOptions()
                .position(destination)
                .title(name));
        //set position and zoom of the camera
        moveCamera(currentLocation, 15);
        //create a URL to make a request to find the path
        String url = getDirectionsUrl(currentLocation, destination);
        FetchUrl fetchUrl = new FetchUrl();
        fetchUrl.setMap(googleMap);
        fetchUrl.execute(url);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    // checking permission for map
    private void getLocationPermission() {

        String[] permission = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
        };
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        geoFire.getLocation(user.getUid(), new LocationCallback() {
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

    private Address geoLocate(String addressName){
        Geocoder geocoder = new Geocoder(this.getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(addressName,1);
        }catch (IOException e){

        }

        if(list.size() > 0){
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

    public void LoadFromFirebase (){
        // get data from orders in firebase
        Query query = databaseReference.child("orders").orderByChild("deliverymanID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(!snapshot.getValue(IncomingData.class).getStatus().equals("done")){
                        incomingData = snapshot.getValue(IncomingData.class);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
