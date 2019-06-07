package com.madness.restaurant.reviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.madness.restaurant.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.RestaurantReviewsHolder> {

    Context context;
    View view;
    List<ReviewsComparable> riderList;

    public ReviewsAdapter(Context context, View view, List<ReviewsComparable> riderList) {
        this.context = context;
        this.view = view;
        this.riderList = riderList;
    }

    @NonNull
    @Override
    public RestaurantReviewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reviews_listitem, viewGroup, false);
        RestaurantReviewsHolder viewHolder = new RestaurantReviewsHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantReviewsHolder holder, int position) {
        final ReviewsComparable riderReviewsComparable = riderList.get(position);
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

    public void updateData(List<ReviewsComparable> viewModels) {
        for (int i = 0; i < viewModels.size(); i++) {
            riderList.set(i, viewModels.get(i));
        }
        notifyDataSetChanged();
    }

    class RestaurantReviewsHolder extends RecyclerView.ViewHolder {

        public TextView name, comment, date;
        RatingBar restSimpleRatingBar;

        public RestaurantReviewsHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            restSimpleRatingBar = itemView.findViewById(R.id.restSimpleRatingBar);
            comment = itemView.findViewById(R.id.rider_comment);
        }
    }
}