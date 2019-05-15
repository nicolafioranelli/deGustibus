package com.madness.deliveryman.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private LocationListener mLocationListener = new LocationListener(LocationManager.PASSIVE_PROVIDER);

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        Log.e(TAG, "newLocation manager: LOCATION_INTERVAL =  " + LOCATION_INTERVAL
                + "\t LOCATION_DISTANCE =  " + LOCATION_DISTANCE);

        /**
         * `LocationManager` provides access to the system location services.
         * These services allow applications to obtain periodic updates of the device's geographical
         * location, or to fire an application-specified Intent when the device enters the proximity
         * of a given geographical location.
         */
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
        }

        /**
         * Register for location updates using the named provider, and a pending intent.
         *
         * provider_name String:    the name of the provider with which to register.
         *                          This value must never be null.
         * minTime	String:         minimum time interval between location updates, in milliseconds
         * minDistance	String:     minimum distance between location updates, in meters
         * listener	String:         a LocationListener whose LocationListener
         *                          #onLocationChanged method will be called for each location update
         *                          This value must never be null.
         */
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListener
            );
        } catch (java.lang.SecurityException ex) {
           ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            try {
                // if the permissions was not previously garanted do not remove the updates
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                // otherwise, remove them
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
