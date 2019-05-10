package com.madness.degustibus.home;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.ArrayList;

public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static final String TAG = HomeHolder.class.getSimpleName();
    public TextView title, description, subtitle;
    public ImageView image;

    /*final private .ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
*/
    private ArrayList<_HomeClass> RestaurantList;

    public _HomeClass getDish(int position) {
        return RestaurantList.get(position);
    }

    public HomeHolder(final View view) {
        super(view);
       // itemView.setOnClickListener(this);
        title = view.findViewById(R.id.rest_title);
        subtitle = view.findViewById(R.id.rest_subtitle);
        description = view.findViewById(R.id.rest_description);
        image = view.findViewById(R.id.rest_imageView);
    }

    @Override
    public void onClick(View v) {
        int clickedPosition = getAdapterPosition();
        //mOnClickListener.onListItemClick(clickedPosition);
    }
}