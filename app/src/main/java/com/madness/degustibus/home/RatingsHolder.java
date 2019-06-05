package com.madness.degustibus.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.madness.degustibus.R;

public class RatingsHolder extends RecyclerView.ViewHolder {

    public TextView name, comment, date;
    public RatingBar rating;

    public RatingsHolder(final View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        comment = itemView.findViewById(R.id.comment);
        date = itemView.findViewById(R.id.date);
        rating = itemView.findViewById(R.id.ratingBar);
    }
}
