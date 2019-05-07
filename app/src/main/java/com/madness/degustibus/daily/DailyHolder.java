package com.madness.degustibus.daily;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.List;

public class DailyHolder extends RecyclerView.ViewHolder {

    private static final String TAG = DailyHolder.class.getSimpleName();
    public TextView dish, type, avail, price;
    public ImageView pic;

    private List<DailyClass> dailyObject;

    public DailyHolder(final View itemView, final List<DailyClass> incomingObject) {
        super(itemView);
        this.dailyObject = dailyObject;
            pic = itemView.findViewById(R.id.dish_icon);
            dish = itemView.findViewById(R.id.dish_name);
            type = itemView.findViewById(R.id.dish_type);
            avail = itemView.findViewById(R.id.dish_quantity);
            price = itemView.findViewById(R.id.dish_price);
    }
}