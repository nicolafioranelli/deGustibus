package com.madness.restaurant.daily;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.R;

public class DishHolder extends RecyclerView.ViewHolder {

    private static final String TAG = DishHolder.class.getSimpleName();
    public TextView dish, desc, avail, price;
    public ImageView pic;

    public DishHolder(final View itemView) {
        super(itemView);
        pic = itemView.findViewById(R.id.dish_icon);
        dish = itemView.findViewById(R.id.dish_name);
        desc = itemView.findViewById(R.id.dish_desc);
        avail = itemView.findViewById(R.id.dish_quantity);
        price = itemView.findViewById(R.id.dish_price);
    }
}