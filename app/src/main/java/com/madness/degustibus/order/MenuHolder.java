package com.madness.degustibus.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madness.degustibus.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuHolder extends RecyclerView.ViewHolder {

    public CircleImageView imageView;
    public TextView title, description, price, quantity;
    public Button plus, minus;


    public MenuHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.restImage);
        title = itemView.findViewById(R.id.rest_title);
        description = itemView.findViewById(R.id.rest_description);
        price = itemView.findViewById(R.id.price);
        quantity = itemView.findViewById(R.id.quantity);
        plus = itemView.findViewById(R.id.buttonPlus);
        minus = itemView.findViewById(R.id.buttonMinus);
    }
}
