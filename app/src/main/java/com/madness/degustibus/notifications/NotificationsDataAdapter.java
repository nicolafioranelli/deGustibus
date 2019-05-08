package com.madness.degustibus.notifications;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.ArrayList;

public class NotificationsDataAdapter extends RecyclerView.Adapter<NotificationsDataAdapter.NotificationsViewHolder> {

    private ArrayList<NotificationsClass> notificationsList;
    final private ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public NotificationsDataAdapter(ArrayList<NotificationsClass> notifications, ItemClickListener listener) {
        this.notificationsList = notifications;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_listitem, parent, false);
        return new NotificationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        NotificationsClass notifications = notificationsList.get(position);
        holder.title.setText(notifications.getTitle());
        holder.date.setText(notifications.getDate());
        holder.description.setText(notifications.getDescription());
        holder.hour.setText(notifications.getHour());
        holder.price.setText(notifications.getPrice());
    }

    @Override
    public int getItemCount() {
        return notificationsList == null ? 0 : notificationsList.size();
    }

    public void remove(int position) {
        notificationsList.remove(position);
        notifyItemRemoved(position);
    }

    public NotificationsClass getNotificationsClass(int position) {
        return notificationsList.get(position);
    }

    public void add(int position, NotificationsClass notificationsClass) {
        notificationsList.add(position, notificationsClass);
        notifyItemInserted(position);
    }

    public ArrayList<NotificationsClass> getList() {
        return notificationsList;
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title, date, description, hour,price;

        public NotificationsViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.rest_title);
            date = view.findViewById(R.id.date);
            description = view.findViewById(R.id.rest_description);
            hour = view.findViewById(R.id.hour);
           // price = view.findViewById(R.id.not_price);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
