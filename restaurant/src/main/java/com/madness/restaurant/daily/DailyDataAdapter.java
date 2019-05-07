package com.madness.restaurant.daily;

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
        //viewHolder = new DailyHolder(layoutView, dailyClasses);
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