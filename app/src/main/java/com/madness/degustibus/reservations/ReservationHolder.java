package com.madness.degustibus.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.madness.degustibus.R;

public class ReservationHolder extends RecyclerView.ViewHolder {

    public TextView status, deliveryman, restaurant, date, hour, price, restaurantAddres;
    public Button recieved;


    public ReservationHolder(final View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        deliveryman = itemView.findViewById(R.id.deliveryman);
        restaurant = itemView.findViewById(R.id.restaurant);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        restaurantAddres = itemView.findViewById(R.id.restaurantAddress);
        recieved = itemView.findViewById(R.id.recievedButton);
    }
}
