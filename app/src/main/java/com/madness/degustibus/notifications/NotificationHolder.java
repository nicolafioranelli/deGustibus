package com.madness.degustibus.notifications;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ArrayList<NotificationsClass> notificationsList;
    private static final String TAG = NotificationHolder.class.getSimpleName();
    public TextView title, date, description, hour,price;
    private List<NotificationsClass> notifications;




    public NotificationHolder(final View view) {
        super(view);
        title = view.findViewById(R.id.rest_title);
        date = view.findViewById(R.id.date);
        description = view.findViewById(R.id.rest_description);
        hour = view.findViewById(R.id.hour);
        price = view.findViewById(R.id.not_price);
        view.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int clickedPosition = getAdapterPosition();
        //mOnClickListener.onListItemClick(clickedPosition);
    }
}