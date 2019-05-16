package com.madness.deliveryman.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Tracker {

    private String userId;
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private static String TAG = "Tracker";

    public Tracker(Context context, String userId) {
        this.context = context;
        this.userId = userId;
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
                                Log.d(TAG, "Latitude: " + location.getLatitude());
                                Log.d(TAG, "Longitude: " + location.getLongitude());
                                storePostionOnFirebase(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });


    }



    public void storePostionOnFirebase(double latitude, double longitude){

        Map<String,Object> map = new HashMap<>();

        map.put("latitude",latitude);
        map.put("longitude",longitude);

        FirebaseDatabase.getInstance().getReference()
                .child("positions")
                .child(userId)
                .setValue(map);
    }
}
