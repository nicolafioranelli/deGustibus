package com.madness.restaurant.Haversine;

import android.support.v4.app.Fragment;

import com.madness.restaurant.R;

public class computeDistance  extends Fragment {
    static final double DEG_RAD = 0.01745329251994;
    static final double R_EARTH = 6371.005;

    double getDistance(Point p1, Point p2) {
        double haversine, distance;
        double dLat, dLon;
        dLat = (p2.getLatitude() - p1.getLatitude()) * DEG_RAD;
        dLon = (p2.getLongitude() - p1.getLongitude()) * DEG_RAD;

        haversine = Math.sin(dLat * 0.5) * Math.sin(dLat * 0.5) +
                Math.sin(dLon * 0.5) * Math.sin(dLon * 0.5) *
                        Math.cos(p1.getLatitude() * DEG_RAD) *
                        Math.cos(p2.getLatitude() * DEG_RAD);

        distance = Math.asin(Math.sqrt(haversine)) * R_EARTH * 2.0;
        //String.format("%.4f", distance).concat(" " + getResources().getString((R.string.sym_km)));
        return distance;
    }
}
