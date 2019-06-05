package com.madness.restaurant.reservations;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;
import com.madness.restaurant.distance.FetchUrl;
import com.madness.restaurant.haversine.Point;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.RiderHolder> implements FetchUrl.AsyncFetchResponse {

    Context context;
    View view;
    List<RiderComparable> riderList;
    ReservationClass orderData;
    HashMap<String, GeoLocation> locations;
    int counter;
    int total;
    FetchUrl fetchUrl;

    public RiderAdapter(Context context, View view, List<RiderComparable> riderList, ReservationClass orderData, HashMap<String, GeoLocation> locations) {
        this.context = context;
        this.view = view;
        this.riderList = riderList;
        this.orderData = orderData;
        this.locations = locations;

        fetchUrl = new FetchUrl();
        //this to set delegate/listener back to this class
        fetchUrl.delegate = this;
    }

    @NonNull
    @Override
    public RiderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_listitem, viewGroup, false);
        RiderHolder viewHolder = new RiderHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RiderHolder holder, int position) {
        final RiderComparable riderComparable = riderList.get(position);
        holder.name.setText(riderComparable.getName());
        String pic;
        if(riderComparable.getPhoto().equals("default")) {
            pic = null;
        } else {
            pic = riderComparable.getPhoto();
        }

        // set picture
        GlideApp.with(holder.imageView.getContext())
                .load(pic)
                .placeholder(R.drawable.user_profile)
                .into(holder.imageView);
        // set availability color
        if (!riderComparable.getAvailable()) {
            holder.status.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        } else {
            holder.status.setColorFilter(context.getResources().getColor(R.color.colorDefault), PorterDuff.Mode.SRC_IN);
        }
        // set the distance
        DecimalFormat df = new DecimalFormat("#.##");
        holder.distance.setText(df.format(riderComparable.getDistance()).concat(" km"));

        //compute the score
        float score = riderComparable.getRating() / (float) riderComparable.getCount();
        holder.score.setRating(score);


        // if it is available, register the click listener
        if (riderComparable.getAvailable()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(riderComparable.getKey());
                    orderData.setDeliverymanID(riderComparable.getKey());
                    NewNotificationClass notification = new NewNotificationClass(context);
                    notification.acceptAndSend(orderData);
                    view.findViewById(R.id.select_rider).setVisibility(View.GONE);
                    view.findViewById(R.id.recyclerView).setVisibility(View.GONE);


                    LatLng origin = new LatLng(locations.get(riderComparable.getName()).latitude, locations.get(riderComparable.getName()).longitude);
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> fromLocationName = null;
                    Double latitude = null;
                    Double longitude = null;
                    try {
                        fromLocationName = geocoder.getFromLocationName(orderData.getCustomerAddress(), 1);

                        if (fromLocationName != null && fromLocationName.size() > 0) {
                            Address a = fromLocationName.get(0);
                            latitude = a.getLatitude();
                            longitude = a.getLongitude();

                            LatLng dest = new LatLng(latitude, longitude);

                            fromLocationName = geocoder.getFromLocationName(orderData.getRestaurantAddress(), 1);
                            if (fromLocationName != null && fromLocationName.size() > 0) {
                                Address a1 = fromLocationName.get(0);
                                latitude = a1.getLatitude();
                                longitude = a1.getLongitude();

                                LatLng rest = new LatLng(latitude, longitude);

                                fetchUrl.execute(getDirectionsUrl(origin, dest, rest));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MAD", "onCallback: ", e);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return riderList.size();
    }

    public void updateData(List<RiderComparable> viewModels) {
        for (int i = 0; i < viewModels.size(); i++) {
            riderList.set(i, viewModels.get(i));
        }
        notifyDataSetChanged();
    }

    class RiderHolder extends RecyclerView.ViewHolder {

        public TextView name, distance;
        public CircleImageView imageView;
        public ImageView status;
        public RatingBar score;

        public RiderHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profileImage);
            status = itemView.findViewById(R.id.status);
            name = itemView.findViewById(R.id.rider);
            distance = itemView.findViewById(R.id.distance);
            score = itemView.findViewById(R.id.ratingBar);

        }
    }

    // update the view when FetchUrl ends adding distance and duration of the road
    @Override
    public void processFetchFinish(Integer distance, String duration, String distanceInt) {
        System.out.println(" url: " + distance);

        FirebaseDatabase.getInstance().getReference().child("distances").child(orderData.getDeliverymanID()).setValue(distance);
    }

    //create URL request
    private String getDirectionsUrl(LatLng origin, LatLng dest, LatLng rest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Rest address
        String str_rest = "waypoints=" + rest.latitude + "," + rest.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        //set mode
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_rest + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyAfDRqzomh-tP7Twu64hMJzWKG4hpG2UmA";
        System.out.println(url);
        return url;
    }
}
