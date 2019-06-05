package com.madness.deliveryman.riderReviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RatingBar;
import com.madness.deliveryman.R;

import java.util.List;

public class RiderReviewsAdapter extends RecyclerView.Adapter<RiderReviewsAdapter.RiderReviewsHolder> {

    Context context;
    View view;
    List<RiderReviewsComparable> riderList;

    public RiderReviewsAdapter(Context context, View view, List<RiderReviewsComparable> riderList) {
        this.context = context;
        this.view = view;
        this.riderList = riderList;
    }

    @NonNull
    @Override
    public RiderReviewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_reviews_listitem, viewGroup, false);
        RiderReviewsHolder viewHolder = new RiderReviewsHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RiderReviewsHolder holder, int position) {
        final RiderReviewsComparable riderReviewsComparable = riderList.get(position);
        holder.name.setText(riderReviewsComparable.getName());
        holder.comment.setText(riderReviewsComparable.getComment());
        holder.date.setText(riderReviewsComparable.getDate());
        holder.restSimpleRatingBar.setNumStars(5);
        holder.restSimpleRatingBar.setRating(Float.valueOf(riderReviewsComparable.getRating()));
    }

    @Override
    public int getItemCount() {
        return riderList.size();
    }

    public void updateData(List<RiderReviewsComparable> viewModels) {
        for (int i = 0; i < viewModels.size(); i++) {
            riderList.set(i, viewModels.get(i));
        }
        notifyDataSetChanged();
    }

    class RiderReviewsHolder extends RecyclerView.ViewHolder {

        public TextView name, comment, date;
        RatingBar restSimpleRatingBar;

        public RiderReviewsHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            restSimpleRatingBar = itemView.findViewById(R.id.restSimpleRatingBar);
            comment = itemView.findViewById(R.id.rider_comment);
        }
    }
}

