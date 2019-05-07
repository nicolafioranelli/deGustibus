package com.madness.deliveryman.incoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.madness.deliveryman.R;

import java.util.List;

public class IncomingHolder extends RecyclerView.ViewHolder {

    private static final String TAG = IncomingHolder.class.getSimpleName();
    public TextView restaurateur;
    public TextView customer;
    public TextView address;
    public TextView date;
    public TextView hour;

    private List<IncomingData> incomingObject;

    public IncomingHolder(final View itemView, final List<IncomingData> incomingObject) {
        super(itemView);
        this.incomingObject = incomingObject;
        restaurateur = itemView.findViewById(R.id.restaurateur);
        customer = itemView.findViewById(R.id.customer);
        address = itemView.findViewById(R.id.address);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);

    }
}