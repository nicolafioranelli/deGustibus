package com.madness.degustibus.home;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.order.OrderFragment;
import com.madness.degustibus.R;

import java.util.ArrayList;

public class HomeDataAdapter extends RecyclerView.Adapter<HomeDataAdapter.HomeViewHolder> implements View.OnClickListener {

    private ArrayList<HomeClass> RestaurantList;
    private Button btn;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    public HomeDataAdapter(ArrayList<HomeClass> reservations) {
        this.RestaurantList = reservations;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurants_listitem, parent, false);
        btn = itemView.findViewById(R.id.button1);
        btn.setOnClickListener(this);
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button1) {
            try {
                fragment = null;
                Class fragmentClass;
                fragmentClass = OrderFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e("MAD", "editProfileClick: ", e);
            }

            ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, fragment, "New order")
                    .addToBackStack("Home")
                    .commit();
        }
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
