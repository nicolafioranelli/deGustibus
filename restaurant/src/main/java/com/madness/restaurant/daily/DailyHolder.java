package com.madness.restaurant.daily;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.R;

public class DailyHolder extends RecyclerView.ViewHolder {

    private static final String TAG = DailyHolder.class.getSimpleName();
    public TextView dish, type, avail, price;
    public ImageView pic;

    public DailyHolder(final View itemView) {
        super(itemView);
        pic = itemView.findViewById(R.id.dish_icon);
        dish = itemView.findViewById(R.id.dish_name);
        type = itemView.findViewById(R.id.dish_type);
        avail = itemView.findViewById(R.id.dish_quantity);
        price = itemView.findViewById(R.id.dish_price);
    }
}