package com.madness.restaurant.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.madness.restaurant.R;
import com.madness.restaurant.daily.DailyHolder;

public class ReservationHolder extends RecyclerView.ViewHolder {

    private static final String TAG = DailyHolder.class.getSimpleName();
    public TextView fullname, identifier, dish, portions, datetime;

    public ReservationHolder(final View itemView) {
        super(itemView);
        fullname = itemView.findViewById(R.id.reservation_fullname);
        identifier = itemView.findViewById(R.id.reservation_identifier);
        dish = itemView.findViewById(R.id.reservation_dish);
        portions = itemView.findViewById(R.id.reservation_portions);
        datetime = itemView.findViewById(R.id.reservation_date_time);
    }

}
