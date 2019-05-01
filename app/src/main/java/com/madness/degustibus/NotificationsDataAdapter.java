package com.madness.degustibus;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationsDataAdapter extends RecyclerView.Adapter<NotificationsDataAdapter.NotificationsViewHolder> {

    private ArrayList<NotificationsClass> notificationsList;

    public NotificationsDataAdapter(ArrayList<NotificationsClass> notifications) {
        this.notificationsList = notifications;
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

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date, description, hour;

        public NotificationsViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            description = view.findViewById(R.id.description);
            hour = view.findViewById(R.id.hour);
        }
    }
}
