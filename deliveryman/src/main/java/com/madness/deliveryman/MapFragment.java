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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Boolean mLocationPermissionGaranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private MapView mapView;
    private GoogleMap googleMap;
    private String restaurantAddress;
    private String customerAddress;
    private String key = "AIzaSyAfDRqzomh-tP7Twu64hMJzWKG4hpG2UmA";
    private DatabaseReference databaseReference;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseUser user;
    private IncomingData incomingData;
    private FusedLocationProviderClient mFusedeLocationProviderClient;
    private Address address;
    LatLng currentLocation;
    Location deviceLocation;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


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
        // Initializing
        MarkerPoints = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        //LoadFromFirebase();
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

        getDeviceLocation();

       /* //if data is not null
        if (incomingData != null) {
            customerAddress = incomingData.getCustomerAddress();
            String restaurantID = incomingData.getRestaurantID();

            //get resturant address from resturant ID by firebase
            Query query = databaseReference.child("restaurants/" + restaurantID + "/address");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    restaurantAddress = (String) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //check status of order
            if (incomingData.getStatus().equals("elaboration")) {
                address = geoLocate(restaurantAddress);
                LatLng destination = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(destination)
                        .title("Restaurant"));
                moveCamera(destination, 15);
                //find path
           /* String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), destination);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.setMap(map);
            fetchUrl.execute(url);*/
          /*  } else if (incomingData.getStatus().equals("delivering")) {
                address = geoLocate(customerAddress);
                LatLng customer = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(customer)
                        .title("Customer"));
                moveCamera(customer, 15);
                //find path
           /* String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), customer);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.setMap(map);
            fetchUrl.execute(url);*/
        /*    }
        }*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void mapOperation(Location devLoc){
        System.out.println("location" + devLoc);
        currentLocation = new LatLng(devLoc.getLatitude(),devLoc.getLongitude());
        LatLng point = currentLocation;
        // Already two locations
        if (MarkerPoints.size() > 1) {
            MarkerPoints.clear();
            googleMap.clear();
        }
        // Adding current location to the ArrayList
        MarkerPoints.add(currentLocation);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        options.position(currentLocation);

        // adding destination
        if (incomingData.getStatus().equals("elaboration")) {
            address = geoLocate(restaurantAddress);
            LatLng restaurant = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerPoints.add(restaurant);
            options.position(restaurant);

        }
        else if (incomingData.getStatus().equals("delivering")){
            address = geoLocate(customerAddress);
            LatLng customer = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerPoints.add(customer);
            options.position(customer);
        }
        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (MarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (MarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        googleMap.addMarker(options);

        // Checks, whether start and end locations are captured
        if (MarkerPoints.size() >= 2) {
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

       /* mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        deviceLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (deviceLocation != null) {
            currentLocation = new LatLng(deviceLocation.getLatitude(),deviceLocation.getLongitude());
            mapOperation();
        }*/

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
    private void getDeviceLocation() {
        mFusedeLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        //check permissions
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //if permissions are denied stop
            return;
        }
            Task location = mFusedeLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener(){

                //once the current position in finded
                @Override
                public void onComplete(@NonNull Task task) {
                    //take address of customer and restaurant from firebase
                    LoadFromFirebase();

                    if(incomingData !=null){
                        // finding destinations
                        customerAddress = incomingData.getCustomerAddress();
                        String restaurantID = incomingData.getRestaurantID();
                        //get resturant address from resturant ID by firebase
                        Query query = databaseReference.child("restaurants/" + restaurantID + "/address");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {restaurantAddress = (String) dataSnapshot.getValue();}

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {            }
                        });
                    }
                    mapOperation((Location)task.getResult());
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

    //create URL request
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        //set mode
        String mode = "mode=bicycling";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
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
