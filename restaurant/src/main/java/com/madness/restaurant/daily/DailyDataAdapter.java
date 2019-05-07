package com.madness.restaurant.daily;
/*
import android.content.Context;
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
        holder.type.setText(daily.getType());
        holder.avail.setText(daily.getAvail());
        holder.price.setText(daily.getPrice() + " €");
        if (daily.getPic() == null) {
            //default pic
            holder.pic.setImageResource(R.drawable.dish_image);
        } else {
            holder.pic.setImageURI(Uri.parse(daily.getPic()));
        }
    }

    @Override
    public int getItemCount() {
        return dailies == null ? 0 : dailies.size();
    }

    public void remove(int position) {
        dailies.remove(position);
        notifyItemRemoved(position);
    }

    public DailyClass getDailyClass(int position) {
        return dailies.get(position);
    }

    public void add(int position, DailyClass daily) {
        dailies.add(position, daily);
        notifyItemInserted(position);
    }

    public ArrayList<DailyClass> getList() {
        return dailies;
    }

    public class DailyViewHolder extends RecyclerView.ViewHolder {
        private TextView dish, type, avail, price;
        private ImageView pic;

        public DailyViewHolder(@NonNull View view) {
            super(view);
            pic = view.findViewById(R.id.dish_icon);
            dish = view.findViewById(R.id.dish_name);
            type = view.findViewById(R.id.dish_type);
            avail = view.findViewById(R.id.dish_quantity);
            price = view.findViewById(R.id.dish_price);
        }
    }
}*/
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import com.madness.restaurant.R;

import java.util.List;

public class DailyDataAdapter extends RecyclerView.Adapter<DailyHolder> {
    protected Context context;
    private List<DailyClass> dailyClasses;

    public DailyDataAdapter(Context context, List<DailyClass> dailyClasses) {
        this.dailyClasses = dailyClasses;
        this.context = context;
    }

    @Override
    public DailyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DailyHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyoffer_listitem, parent, false);
        viewHolder = new DailyHolder(layoutView, dailyClasses);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DailyHolder holder, int position) {
        DailyClass daily = dailyClasses.get(position);
        holder.dish.setText(dailyClasses.get(position).getDish());
        holder.type.setText(dailyClasses.get(position).getType());
        holder.avail.setText(dailyClasses.get(position).getAvail());
        holder.price.setText(dailyClasses.get(position).getPrice());
        //holder.pic.setText(dailyClasses.get(position).getPic());
        if (daily.getPic() == null) {
            //default pic
            holder.pic.setImageResource(R.drawable.dish_image);
        } else {
            holder.pic.setImageURI(Uri.parse(daily.getPic()));
        }
    }
    public void remove(int position) {
        dailyClasses.remove(position);
        notifyItemRemoved(position);
    }
    public void add(int position, DailyClass daily) {
        dailyClasses.add(position, daily);
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return this.dailyClasses.size();
    }
}