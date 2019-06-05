package com.madness.restaurant.distance;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {

    public Integer parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONObject jDistance;
        JSONObject jDuration;
        Integer distance = 0;


        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    /** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    //hmDistance.put("distance", jDistance.getString("text"));
                    //hmDistance.put("distanceInt", String.valueOf(jDistance.getInt("value")));
                    distance += jDistance.getInt("value");
                    System.out.println("Leg dist: " + jDistance.getInt("value"));
                    /** Getting duration from the json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));
                    /** Adding distance object to the path */
                    path.add(hmDistance);
                    /** Adding duration object to the path */
                    path.add(hmDuration);

                    routes.add(path);
                    System.out.println("Distance: " + distance);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return distance;
    }


}
