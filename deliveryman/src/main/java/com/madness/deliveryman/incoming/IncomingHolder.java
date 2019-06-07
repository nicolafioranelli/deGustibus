package com.madness.deliveryman.incoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madness.deliveryman.R;

public class IncomingHolder extends RecyclerView.ViewHolder {

    public TextView status, customer, restaurant, date, hour, price, customerAddress, restaurantAddres;
    public Button button, refuse, map;


    public IncomingHolder(final View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        customer = itemView.findViewById(R.id.customer);
        restaurant = itemView.findViewById(R.id.restaurant);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        button = itemView.findViewById(R.id.orderButton);
        refuse = itemView.findViewById(R.id.refuseButton);
        customerAddress = itemView.findViewById(R.id.customerAddress);
        restaurantAddres = itemView.findViewById(R.id.restaurantAddress);
        map = itemView.findViewById(R.id.maps);
    }
}
