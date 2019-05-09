package com.madness.degustibus.order;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.ArrayList;

public class MenuHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MenuHolder.class.getSimpleName();
    public TextView title, description, price, quantity;
    public ImageView image;
    public Button buttonPlus;
    public Button buttonMinus;

    private ArrayList<MenuClass> dishList;

    public MenuClass getDish(int position) {
        return dishList.get(position);
    }

    public MenuHolder(final View view) {
        super(view);
        title = view.findViewById(R.id.rest_title);
        description = view.findViewById(R.id.rest_description);
        price = view.findViewById(R.id.price);
        quantity = view.findViewById(R.id.quantity);
        image = view.findViewById(R.id.rest_imageView);
        buttonMinus = view.findViewById(R.id.buttonMinus);
        buttonPlus = view.findViewById(R.id.buttonPlus);
    }
}