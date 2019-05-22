package com.madness.deliveryman;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


class FetchUrl extends AsyncTask<String, Void, String> {
    GoogleMap map;


    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String data = "";
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
        } catch (Exception e) {

        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser();
        parserTask.setMap(map);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        //data = "{   \"geocoded_waypoints\" : [      {         \"geocoder_status\" : \"OK\",         \"place_id\" : \"ChIJfwsXrfRsiEcRtPYIT8rO71A\",         \"types\" : [ \"street_address\" ]      },      {         \"geocoder_status\" : \"OK\",         \"place_id\" : \"ChIJwz5E6B5tiEcRArVymwT6yiU\",         \"types\" : [ \"street_address\" ]      }   ],   \"routes\" : [      {         \"bounds\" : {            \"northeast\" : {               \"lat\" : 45.0794095,               \"lng\" : 7.656293900000001            },            \"southwest\" : {               \"lat\" : 45.06404329999999,               \"lng\" : 7.633696            }         },         \"copyrights\" : \"Map data ©2019 Google\",         \"legs\" : [            {               \"distance\" : {                  \"text\" : \"3.5 km\",                  \"value\" : 3523               },               \"duration\" : {                  \"text\" : \"10 mins\",                  \"value\" : 596               },               \"end_address\" : \"Via Pier Carlo Boggio, 65a, 10138 Torino TO, Italy\",               \"end_location\" : {                  \"lat\" : 45.0646847,                  \"lng\" : 7.656293900000001               },               \"start_address\" : \"Via Carlo Capelli, 27, 10146 Torino TO, Italy\",               \"start_location\" : {                  \"lat\" : 45.07771959999999,                  \"lng\" : 7.633696               },               \"steps\" : [                  {                     \"distance\" : {                        \"text\" : \"0.2 km\",                        \"value\" : 189                     },                     \"duration\" : {                        \"text\" : \"1 min\",                        \"value\" : 65                     },                     \"end_location\" : {                        \"lat\" : 45.0794095,                        \"lng\" : 7.633953799999999                     },                     \"html_instructions\" : \"Head \\u003cb\\u003enorth\\u003c/b\\u003e on \\u003cb\\u003eVia Carlo Capelli\\u003c/b\\u003e toward \\u003cb\\u003eVia Giacinto Pacchiotti\\u003c/b\\u003e\",                     \"polyline\" : {                        \"points\" : \"wfcrGs}qm@s@GsD[iBM\"                     },                     \"start_location\" : {                        \"lat\" : 45.07771959999999,                        \"lng\" : 7.633696                     },                     \"travel_mode\" : \"DRIVING\"                  },                  {                     \"distance\" : {                        \"text\" : \"0.3 km\",                        \"value\" : 259                     },                     \"duration\" : {                        \"text\" : \"1 min\",                        \"value\" : 58                     },                     \"end_location\" : {                        \"lat\" : 45.079297,                        \"lng\" : 7.637246699999999                     },                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eVia Vittorio Asinari di Bernezzo\\u003c/b\\u003e\",                     \"maneuver\" : \"turn-right\",                     \"polyline\" : {                        \"points\" : \"iqcrGe_rm@B{AFkE?m@BgCDuD\"                     },                     \"start_location\" : {                        \"lat\" : 45.0794095,                        \"lng\" : 7.633953799999999                     },                     \"travel_mode\" : \"DRIVING\"                  },                  {                     \"distance\" : {                        \"text\" : \"0.5 km\",                        \"value\" : 472                     },                     \"duration\" : {                        \"text\" : \"1 min\",                        \"value\" : 81                     },                     \"end_location\" : {                        \"lat\" : 45.0752451,                        \"lng\" : 7.6354564                     },                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eCorso Monte Grappa\\u003c/b\\u003e\",                     \"maneuver\" : \"turn-right\",                     \"polyline\" : {                        \"points\" : \"spcrGysrm@pC~@`Cr@jHlC|Bz@tA\\\\TH\"                     },";
        return data;
    }

     void setMap (GoogleMap map){
        this.map = map;
    }
}
