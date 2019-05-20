package com.madness.restaurant.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderHolder extends RecyclerView.ViewHolder {

    public TextView name, distance;
    public CircleImageView imageView;
    public ImageView status;

    public RiderHolder(final View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.profileImage);
        status = itemView.findViewById(R.id.status);
        name = itemView.findViewById(R.id.rider);
        distance = itemView.findViewById(R.id.distance);
    }

}
