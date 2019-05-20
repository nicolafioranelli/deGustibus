package com.madness.deliveryman.incoming;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.madness.deliveryman.R;

public class IncomingHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    public TextView status, customer, customerAddress, restaurant, restaurantAddress, date, hour, price;
    public Button button, refuse;
    public GoogleMap map;
    private Context context;

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

        context = itemView.getContext();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        map = googleMap;
        setMapLocation();
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