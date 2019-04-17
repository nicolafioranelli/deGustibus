package com.madness.restaurant.daily;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class DailyDataAdapter extends RecyclerView.Adapter<DailyDataAdapter.DailyViewHolder> {
    private ArrayList<DailyClass> dailies;

    public DailyDataAdapter(ArrayList<DailyClass> dailies) {
        this.dailies = dailies;
    }
    public class DailyViewHolder extends RecyclerView.ViewHolder{
        private TextView dish, type, avail, price;
        private ImageView pic;

        public DailyViewHolder(@NonNull View view) {
            super(view);
            pic = (ImageView) view.findViewById(R.id.dish_icon);
            dish = (TextView) view.findViewById(R.id.dish_name);
            type = (TextView) view.findViewById(R.id.dish_type);
            avail = (TextView) view.findViewById(R.id.dish_quantity);
            price = (TextView) view.findViewById(R.id.dish_price);

        }
    }
    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailyoffer_listitem, parent, false);

        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int i) {
        DailyClass daily = dailies.get(i);
        holder.dish.setText(daily.getDish());
        holder.pic.setImageURI(Uri.parse(daily.getPic()));
        holder.type.setText(daily.getType());
        holder.avail.setText(daily.getAvail());
        holder.price.setText(daily.getPrice());
    }

    @Override
    public int getItemCount() {
        return  dailies == null ? 0 : dailies.size();
    }
    public void remove(int position) {
        dailies.remove(position);
        notifyItemRemoved(position);
    }
    public DailyClass getDailyClass(int position) {
        return dailies.get(position);
    }
    public void add (int position, DailyClass daily) {
        dailies.add(position, daily);
        notifyItemInserted(position);
    }
    public ArrayList<DailyClass> getList () {
        return dailies;
    }
}
