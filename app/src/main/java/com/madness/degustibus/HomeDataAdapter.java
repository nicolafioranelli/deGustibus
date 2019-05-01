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

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.HomeViewHolder> {

    private ArrayList<HomeClass> RestaurantList;

    public HomeDataAdapter(ArrayList<HomeClass> reservations) {
        this.RestaurantList = reservations;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurants_listitem, parent, false);
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeClass restaurant = RestaurantList.get(position);
        holder.title.setText(restaurant.getTitle());
        holder.subtitle.setText(restaurant.getSubtitle());
        holder.description.setText(restaurant.getDescription());
        if (restaurant.getPic() == null) {
            // Set default image
            holder.image.setImageResource(R.drawable.restaurant);
        } else {
            holder.image.setImageURI(Uri.parse(restaurant.getPic()));
        }
    }

    @Override
    public int getItemCount() {
        return RestaurantList == null ? 0 : RestaurantList.size();
    }

    public void remove(int position) {
        RestaurantList.remove(position);
        notifyItemRemoved(position);
    }

    public HomeClass getHomeClass(int position) {
        return RestaurantList.get(position);
    }

    public HomeClass getReservation(int position) {
        return RestaurantList.get(position);
    }

    public void add(int position, HomeClass reservationClass) {
        RestaurantList.add(position, reservationClass);
        notifyItemInserted(position);
    }

    public ArrayList<HomeClass> getList() {
        return RestaurantList;
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView title, subtitle, description;
        private ImageView image;

        public HomeViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.imageView);
        }
    }
}
