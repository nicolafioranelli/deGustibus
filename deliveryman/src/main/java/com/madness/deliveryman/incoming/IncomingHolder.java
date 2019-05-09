package com.madness.deliveryman.incoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.madness.deliveryman.R;

public class IncomingHolder extends RecyclerView.ViewHolder {

    private static final String TAG = IncomingHolder.class.getSimpleName();
    public TextView restaurantName;
    public TextView restaurantAddress;
    public TextView customerName;
    public TextView customerAddress;
    public TextView customerPhone;
    public TextView date;
    public TextView hour;

    public IncomingHolder(final View itemView) {
        super(itemView);
        restaurantName = itemView.findViewById(R.id.restaurateur);
        restaurantAddress = itemView.findViewById(R.id.restaurateurAddress);
        customerName = itemView.findViewById(R.id.customer);
        customerAddress = itemView.findViewById(R.id.address);
        customerPhone = itemView.findViewById(R.id.costumerPhone);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
    }
}