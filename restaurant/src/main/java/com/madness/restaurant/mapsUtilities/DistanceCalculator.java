package com.madness.restaurant.mapsUtilities;

import android.location.Address;
import android.location.Geocoder;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DistanceCalculator {

    private String from;
    private String to;
    private static String KEY = "AIzaSyAfDRqzomh-tP7Twu64hMJzWKG4hpG2UmA";

    public DistanceCalculator(String from, String to) {
        this.from = from;
        this.to = to;
    }

    private String makeURL (){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(from);
        urlString.append("&destination=");// to
        urlString.append(to);
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=");
        urlString.append(KEY);
        return urlString.toString();
    }

    public double getDistance(){
        double result = 0;

        String url = makeURL();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final JSONObject json;
                        try {

                            json = new JSONObject(response);
                            /*JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);
                            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                            String encodedString = overviewPolylines.getString("points");*/
                            System.out.println(json.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // do nothing
                    }
                });

        return result;
    }
}
