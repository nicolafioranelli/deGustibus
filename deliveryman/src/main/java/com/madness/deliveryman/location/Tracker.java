package com.madness.deliveryman.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class Tracker {

    private String userId;
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private static String TAG = "Tracker";
    private LocationCallback locationCallback;
    private boolean startUpdates = false;
    private static int DELAY = 60; // in seconds

    public Tracker(Context context, String userId, LocationCallback locationCallback) {
        this.context = context;
        this.userId = userId;
        this.locationCallback = locationCallback;
    }

    public void storeTheFirstPosition() {

        // this check is compulsory in order to build correctly the project
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "LATITUDE: " + location.getLatitude());
                            Log.d(TAG, "LONGITUDE: " + location.getLongitude());
                            storePostionOnFirebase(location.getLatitude(), location.getLongitude());
                        }
                    }
                });


        // setup the next updates
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(DELAY * 1000); //
        //locationRequest.setFastestInterval(interval*1000); // 1 second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //TODO check those listeners @ https://developer.android.com/training/location/change-location-settings.html

        /*task.addOnSuccessListener((Executor) this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });*/

        /*task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(context,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });*/


        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "callback LATITUDE " + location.getLatitude());
                    Log.d(TAG, "callback LONGITUDE " + location.getLongitude());
                    storePostionOnFirebase(location.getLatitude(), location.getLongitude());
                }
            }
        };

        startLocationUpdates();


    }



    public void startLocationUpdates() {

        // this check is compulsory in order to build correctly the project
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null);
        startUpdates = true;
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }



    public void storePostionOnFirebase(double latitude, double longitude){

        Map<String,Object> map = new HashMap<>();
        map.put("latitude",latitude);
        map.put("longitude",longitude);

        FirebaseDatabase.getInstance().getReference()
                .child("positions")
                .child(userId)
                .updateChildren(map);
    }

    public boolean isStartUpdates() {
        return startUpdates;
    }
}
