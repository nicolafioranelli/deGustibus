package com.madness.deliveryman.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.madness.deliveryman.R;

public class NotificationHolder extends RecyclerView.ViewHolder {

    private static final String TAG = NotificationHolder.class.getSimpleName();
    public TextView type, description, date;
    public Button button;

    public NotificationHolder(final View itemView) {
        super(itemView);
        type = itemView.findViewById(R.id.type);
        description = itemView.findViewById(R.id.description);
        date = itemView.findViewById(R.id.date);
        button = itemView.findViewById(R.id.button_delete);
    }
}
