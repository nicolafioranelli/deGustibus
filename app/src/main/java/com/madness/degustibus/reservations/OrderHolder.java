package com.madness.degustibus.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.madness.degustibus.R;

public class OrderHolder extends RecyclerView.ViewHolder {

    public TextView status, deliveryman, restaurant, date, hour, price;
    public ImageButton button;


    public OrderHolder(final View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        deliveryman = itemView.findViewById(R.id.deliveryman);
        restaurant = itemView.findViewById(R.id.restaurant);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        button = itemView.findViewById(R.id.button);
    }

}
