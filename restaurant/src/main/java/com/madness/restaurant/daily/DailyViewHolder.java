package com.madness.restaurant.daily;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.R;

public class DailyViewHolder extends RecyclerView.ViewHolder {
    View view;
    public DailyViewHolder(@NonNull View itemView) {
        super(itemView);
        view=itemView;
    }

    public void setDeteils(Context context, String dishS, String typeS, String availS, String priceS, String image ){

        TextView dish, type, avail, price;
        ImageView pic;
            pic = view.findViewById(R.id.dish_icon);
            dish = view.findViewById(R.id.dish_name);
            type = view.findViewById(R.id.dish_type);
            avail = view.findViewById(R.id.dish_quantity);
            price = view.findViewById(R.id.dish_price);

        dish.setText(dishS);
        type.setText(typeS);
        avail.setText(availS);
        price.setText(priceS);
        if (image == null) {
            //default pic
            pic.setImageResource(R.drawable.dish_image);
        } else {
            pic.setImageURI(Uri.parse(image));
        }
    }
}
