package com.madness.restaurant.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madness.restaurant.R;
import com.madness.restaurant.reservations.ReservationClass;

import java.util.ArrayList;
import java.util.List;

class ReservationsDataAdapter extends RecyclerView.Adapter<ReservationsDataAdapter.ReservationViewHolder> {
    private ArrayList<ReservationClass> reservations;

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView name, identifier, seats, date, time;

        public ReservationViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.reservation_fullname);
            identifier = (TextView) view.findViewById(R.id.reservation_identifier);
            seats = (TextView) view.findViewById(R.id.reservation_seats);
            date = (TextView) view.findViewById(R.id.reservation_date);
            time = (TextView) view.findViewById(R.id.reservation_time);
        }
    }

    public ReservationsDataAdapter(ArrayList<ReservationClass> reservations) {
        this.reservations = reservations;
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_listitem, parent, false);

        return new ReservationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        ReservationClass reservation = reservations.get(position);
        holder.name.setText(reservation.getName());
        holder.identifier.setText(String.valueOf(reservation.getIdentifier()));
        holder.seats.setText(reservation.getSeats());
        holder.date.setText(reservation.getDate());
        holder.time.setText(reservation.getTime());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void remove(int position) {
        reservations.remove(position);
        notifyItemRemoved(position);
    }
    public ReservationClass getReservation(int position) {
        return reservations.get(position);
    }
    public void add (int position, ReservationClass reservationClass) {
        reservations.add(position, reservationClass);
        notifyItemInserted(position);
    }
    public ArrayList<ReservationClass> getList () {
        return reservations;
    }
}