package com.madness.restaurant.distance;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FetchUrl extends AsyncTask<String, Void, String>  {
    public AsyncFetchResponse delegate = null;



    //interface to comunicate with MapFragment
    public interface AsyncFetchResponse {
        void processFetchFinish(Integer distance, String duration, String distanceInt);
    }


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
        JSONObject jObject;
        Integer distance = null;

        try {
            jObject = new JSONObject(s);
            DataParser parser = new DataParser();
            //start parsing data
            distance = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        delegate.processFetchFinish(distance,null,null);
        System.out.println("Fetch url: " + distance);
    }

    /**
     * A method to download json data from url
     */
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

        return data;
    }

}
