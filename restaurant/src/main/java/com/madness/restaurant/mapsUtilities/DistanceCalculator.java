package com.madness.restaurant.mapsUtilities;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DistanceCalculator {

    private static String KEY = "AIzaSyAfDRqzomh-tP7Twu64hMJzWKG4hpG2UmA";
    private String from;
    private String to;
    private Context context;
    private double result;

    public DistanceCalculator(Context context) {
        this.context = context;
    }

    private String makeURL() {
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

    public void computeDistance(final DistanceCallback callback) {
        result = 0;
        String url = makeURL();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            final JSONObject json = new JSONObject(response);
                            JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);
                            JSONArray legsArray = routes.getJSONArray("legs");
                            JSONObject legs = legsArray.getJSONObject(0);
                            JSONObject distance = legs.getJSONObject("distance");
                            result = distance.getDouble("value");
                            callback.onDistanceComputed(result);
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

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(double latitude, double longitude) {
        this.from = latitude + "," + longitude;
    }

    public void setTo(double latitude, double longitude) {
        this.to = latitude + "," + longitude;
    }

    public interface DistanceCallback {
        void onDistanceComputed(double distance);
    }
}
