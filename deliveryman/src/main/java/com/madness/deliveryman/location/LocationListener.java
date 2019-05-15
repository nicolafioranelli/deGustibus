package com.madness.deliveryman.location;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class LocationListener implements  android.location.LocationListener {

    private Location mLastLocation;
    private static final String TAG = "LocationListener";

    public LocationListener(String passiveProvider) {
        Log.d(TAG, "LocationListener " + passiveProvider);
        mLastLocation = new Location(passiveProvider);
        System.out.println("Latitude: " + mLastLocation.getLatitude());
        System.out.println("Longitude: " + mLastLocation.getLongitude());
    }

    @Override
    public void onLocationChanged(Location provider) {
        Log.e(TAG, "LocationListener " + provider);
        // TODO store those values in FireBase
        System.out.println("Latitude: " + provider.getLatitude());
        System.out.println("Longitude: " + provider.getLongitude());

        mLastLocation = new Location(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "onProviderDisabled: " + provider);
    }
}
