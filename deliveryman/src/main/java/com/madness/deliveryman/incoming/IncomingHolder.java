package com.madness.deliveryman.incoming;

import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.animation.Positioning;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madness.deliveryman.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class IncomingHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    public TextView status, customer, customerAddress, restaurant, restaurantAddress, date, hour, price;
    public Button button, refuse;
    public MapView map;
    private GoogleMap googleMap;
    private Context context;
    private LatLng location;

    public IncomingHolder(final View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        customer = itemView.findViewById(R.id.customer);
        customerAddress = itemView.findViewById(R.id.custAddress);
        restaurant = itemView.findViewById(R.id.restaurant);
        restaurantAddress = itemView.findViewById(R.id.restAddress);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        button = itemView.findViewById(R.id.orderButton);
        refuse = itemView.findViewById(R.id.refuseButton);
        map = itemView.findViewById(R.id.map);
        context = itemView.getContext();

        if (map != null) {
            // Initialise the MapView
            map.onCreate(null);
            // Set the map ready callback to receive the GoogleMap object
            map.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        this.googleMap = googleMap;
    }

    public void setMapLocation(String locationName){
        if  (googleMap == null) return;
        if  (locationName == null) return;

        Geocoder geocoder = new Geocoder(
                context,
                Locale.getDefault()
        );

        List<Address>   fromLocationName    = null;
        Double          latitude            = null;
        Double          longitude           = null;


        try {
            fromLocationName = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fromLocationName != null && fromLocationName.size() > 0) {
            Address a = fromLocationName.get(0);
            latitude = a.getLatitude();
            longitude = a.getLongitude();

            this.location = new LatLng(latitude,longitude);

            // Add a marker for this item and set the camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.location, 13f));
            googleMap.addMarker(new MarkerOptions().position(this.location));


    }



    //TODO https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/LiteListDemoActivity.java
    /*private void setMapLocation() {
        if (map == null) return;

        NamedLocation data = (NamedLocation) mapView.getTag();
        if (data == null) return;

        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));
        map.addMarker(new MarkerOptions().position(data.location));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void bindView(int pos) {
        NamedLocation item = namedLocations[pos];
        // Store a reference of the ViewHolder object in the layout.
        layout.setTag(this);
        // Store a reference to the item in the mapView's tag. We use it to get the
        // coordinate of a location, when setting the map location.
        mapView.setTag(item);
        setMapLocation();
        title.setText(item.name);
    }*/
}
}