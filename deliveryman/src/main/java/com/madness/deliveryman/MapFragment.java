package com.madness.deliveryman;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.incoming.IncomingData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private Boolean mLocationPermissionGaranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private MapView mapView;
    private GoogleMap googleMap;
    LatLng deliveryAddress;
    private String restaurantAddress = "poliTO";
    private String customerAddress = "via carlo capelli 27";
    private String key = "AIzaSyAfDRqzomh-tP7Twu64hMJzWKG4hpG2UmA";
    private DatabaseReference databaseReference;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser user;
    private IncomingData incomingData;
    private FusedLocationProviderClient mFusedeLocationProviderClient;
    private Address address;
    private Location currentLocation;


    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();
        user = FirebaseAuth.getInstance().getCurrentUser();
        incomingData = null;
        currentLocation = null;


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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        if (mLocationPermissionGaranted) {

            //check for permissions
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            //get location and put marker on it
            getDeviceLocation();
            googleMap.setMyLocationEnabled(true);

            if(incomingData != null){
                customerAddress = incomingData.getCustomerAddress();
                String restaurantID = incomingData.getRestaurantID();

                //get resturant address
                Query query = databaseReference.child("restaurants/"+restaurantID + "/address");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        restaurantAddress = (String) dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (incomingData.getStatus().equals("elaboration")){
                    address = geoLocate(restaurantAddress);
                    LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(destination)
                            .title("Restaurant"));
                    moveCamera(destination,15);
                    //find path
               /* String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), destination);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.setMap(map);
                fetchUrl.execute(url);*/
                }
                else if (incomingData.getStatus().equals("delivering")){
                    address = geoLocate(customerAddress);
                    LatLng customer =new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(customer)
                            .title("Customer"));
                    moveCamera(customer,15);
                    //find path
               /* String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), customer);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.setMap(map);
                fetchUrl.execute(url);*/
                }
            }
        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // checking permission for map
    private void getLocationPermission(){

        String[] permission = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
        };
        if(ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGaranted = true;
            } else {
                ActivityCompat.requestPermissions(this.getActivity(),permission,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this.getActivity(), permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    //getting the device current location
    private void getDeviceLocation(){
        mFusedeLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        try{
            if(mLocationPermissionGaranted){
                Task location = mFusedeLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            //found location
                            currentLocation = (Location) task.getResult();
                            //move camera to current location
                        }
                        else{
                            //current location is null -> unable to get current location
                        }
                    }
                });
            }

        }catch(SecurityException e){

        }
    }
    //moving the camera to latLng with zoom
    private void moveCamera(LatLng latLng,float zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + key;
        return url;
    }

    public void LoadFromFirebase (){
        // obtain the url /offers/{restaurantIdentifier}
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
