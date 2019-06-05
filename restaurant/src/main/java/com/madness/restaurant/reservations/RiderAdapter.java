package com.madness.restaurant.reservations;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;
import com.madness.restaurant.mapsUtilities.DistanceCalculator;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.RiderHolder> {

    Context context;
    View view;
    List<RiderComparable> riderList;
    ReservationClass orderData;
    HashMap<String, GeoLocation> locations;
    int counter;
    int total;

    private double totalDistance;

    public RiderAdapter(Context context, View view, List<RiderComparable> riderList, ReservationClass orderData, HashMap<String,GeoLocation> locations) {
        this.context = context;
        this.view = view;
        this.riderList = riderList;
        this.orderData = orderData;
        this.locations = locations;
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

                    riderComparable.getDistance(); //distance between restaurant and delveryman
                    orderData.getCustomerAddress(); // customare address
                    // obtain the rider position in `positions`

                    /*DistanceCalculator calculator = new DistanceCalculator(context);


                    calculator.setFrom(locations.get(riderComparable.getName()).latitude,
                            locations.get(riderComparable.getName()).longitude);
                    calculator.setTo(orderData.getRestaurantAddress());
                    calculator.computeDistance(new DistanceCalculator.DistanceCallback() {
                        @Override
                        public void onDistanceComputed(double distance) {
                            totalDistance += distance;
                        }
                    });


                    calculator.setFrom(orderData.getCustomerAddress());
                    calculator.setTo(orderData.getRestaurantAddress());
                    calculator.computeDistance(new DistanceCalculator.DistanceCallback() {
                        @Override
                        public void onDistanceComputed(double distance) {
                            totalDistance += distance;
                        }
                    });*/

                    DistanceCalculator calculatorA = new DistanceCalculator(context);
                    calculatorA.setFrom(
                            locations.get(riderComparable.getName()).latitude,
                            locations.get(riderComparable.getName()).longitude
                    );
                    calculatorA.setTo(orderData.getRestaurantAddress());
                    calculatorA.computeDistance(new DistanceCalculator.DistanceCallback() {
                        @Override
                        public void onDistanceComputed(double distance) {
                            totalDistance += distance;

                            DistanceCalculator calculatorB = new DistanceCalculator(context);
                            calculatorB.setFrom(orderData.getRestaurantAddress());
                            calculatorB.setTo(orderData.getCustomerAddress());
                            calculatorB.computeDistance(new DistanceCalculator.DistanceCallback() {
                                @Override
                                public void onDistanceComputed(double distance) {
                                    totalDistance += distance;

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("distances")
                                            .child(orderData.getDeliverymanID())
                                            .setValue(totalDistance);
                                }
                            });

                        }
                    });

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
}
