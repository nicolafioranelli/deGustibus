package com.madness.restaurant.reservations;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.RiderHolder> {

    Context context;
    List<RiderComparable> riderList;

    public RiderAdapter(Context context, List<RiderComparable> riderList) {
        this.context = context;
        this.riderList = riderList;
    }

    @NonNull
    @Override
    public RiderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_listitem, viewGroup, false);
        RiderHolder viewHolder = new RiderHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RiderHolder holder, int position) {
        final RiderComparable riderComparable = riderList.get(position);
        holder.name.setText(riderComparable.getName());
        GlideApp.with(holder.imageView.getContext())
                .load(riderComparable.getPhoto())
                .placeholder(R.drawable.user_profile)
                .into(holder.imageView);
        if (!riderComparable.getAvailable()) {
            holder.status.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        holder.distance.setText(df.format(riderComparable.getDistance()).concat(" km"));

        if (!riderComparable.getAvailable()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(riderComparable.getKey());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return riderList.size();
    }

    public void updateData(List<RiderComparable> viewModels) {
        for (int i = 0; i < viewModels.size(); i++) {
            riderList.set(i, viewModels.get(i));
        }
        notifyDataSetChanged();
    }

    class RiderHolder extends RecyclerView.ViewHolder {

        public TextView name, distance;
        public CircleImageView imageView;
        public ImageView status;

        public RiderHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profileImage);
            status = itemView.findViewById(R.id.status);
            name = itemView.findViewById(R.id.rider);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
