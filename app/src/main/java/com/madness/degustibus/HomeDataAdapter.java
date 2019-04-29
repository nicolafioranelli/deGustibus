package com.madness.degustibus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.HomeViewHolder> {

    private ArrayList<HomeClass> RestaurantList;
    private Context context;

    public HomeDataAdapter(ArrayList<HomeClass> reservations) {
        this.RestaurantList = reservations;
        this.context = context;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurants_listitem, parent, false);
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        HomeClass restaurant = RestaurantList.get(position);
        holder.title.setText(restaurant.getTitle());
        holder.subtitle.setText(restaurant.getSubtitle());
        holder.description.setText(restaurant.getDescription());
        /*holder.image.setText(restaurant.getDescription());
        if(restaurant.getPic() == null) {
            holder.image.setImageResource(R.drawable.restImage);
        } else {
            holder.image.setImageURI(Uri.parse(restaurant.getPic()));
        }*/
    }

    @Override
    public int getItemCount() {
        return RestaurantList.size();
    }

    public void remove(int position) {
        RestaurantList.remove(position);
        notifyItemRemoved(position);
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

        public HomeViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);
            description = view.findViewById(R.id.description);
            //image = view.findViewById(R.id.imageView);
        }
    }
}
