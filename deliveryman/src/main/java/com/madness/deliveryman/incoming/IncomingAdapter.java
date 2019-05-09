package com.madness.deliveryman.incoming;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.madness.deliveryman.R;
import java.util.List;

public class IncomingAdapter/* extends RecyclerView.Adapter<IncomingHolder>*/ {/*
    protected Context context;
    private List<IncomingData> incomingData;

    public IncomingAdapter(Context context, List<IncomingData> incomingData) {
        this.incomingData = incomingData;
        this.context = context;
    }

    @Override
    public IncomingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IncomingHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_item, parent, false);
        viewHolder = new IncomingHolder(layoutView, incomingData);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IncomingHolder holder, int position) {
        holder.restaurateur.setText(incomingData.get(position).getRestaurateur());
        holder.customer.setText(incomingData.get(position).getCustomer());
        holder.address.setText(incomingData.get(position).getAddress());
        holder.date.setText(incomingData.get(position).getDate());
        holder.hour.setText(incomingData.get(position).getHour());
    }

    @Override
    public int getItemCount() {
        return this.incomingData.size();
    }*/
}