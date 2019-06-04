package com.madness.degustibus.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.madness.degustibus.R;

public class ItemHolder extends RecyclerView.ViewHolder {

    public TextView foodName, quantity;
    public RatingBar ratingBar;

    public ItemHolder(final View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.foodName);
        quantity = itemView.findViewById(R.id.quantity);
        ratingBar = itemView.findViewById(R.id.ratingBar);
    }

}
