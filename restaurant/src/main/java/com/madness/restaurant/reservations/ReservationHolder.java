package com.madness.restaurant.reservations;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.madness.restaurant.R;
import com.madness.restaurant.daily.DailyHolder;

public class ReservationHolder extends RecyclerView.ViewHolder {

    public TextView status, customer, description, date, hour, price;
    public ImageButton button;

    public ReservationHolder(final View itemView) {
        super(itemView);
        status = itemView.findViewById(R.id.status);
        customer = itemView.findViewById(R.id.customer);
        description = itemView.findViewById(R.id.description);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        button = itemView.findViewById(R.id.button);
    }

}
