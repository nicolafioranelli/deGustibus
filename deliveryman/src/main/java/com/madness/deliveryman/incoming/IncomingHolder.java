package com.madness.deliveryman.incoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madness.deliveryman.R;

public class IncomingHolder extends RecyclerView.ViewHolder {

    public TextView status, customer, customerAddress, restaurant, restaurantAddress, date, hour, price;
    public Button button, refuse;

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
    }
}