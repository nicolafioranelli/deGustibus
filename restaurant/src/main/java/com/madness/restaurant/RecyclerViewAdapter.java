package com.madness.restaurant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;

    private ArrayList<String> _names;
    private ArrayList<String> _identifiers;
    private ArrayList<String> _seats;
    private ArrayList<String> _dates;
    private ArrayList<String> _time;


    public RecyclerViewAdapter(Context mContext, ArrayList<String> _names, ArrayList<String> _identifiers, ArrayList<String> _seats, ArrayList<String> _dates, ArrayList<String> _time) {
        this.mContext = mContext;
        this._names = _names;
        this._identifiers = _identifiers;
        this._seats = _seats;
        this._dates = _dates;
        this._time = _time;
    }

    // it inflate the view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout._reservation_listitem, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fullname.setText(_names.get(i));
        viewHolder.identifier.setText("reservation #"+_identifiers.get(i));
        viewHolder.seats.setText(_seats.get(i) + " seats");
        viewHolder.date_time.setText(_dates.get(i) + " " + _time.get(i));
    }

    @Override
    public int getItemCount() {
        return _names.size();
    }


    // -- INNER CLASS --
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView fullname;
        TextView identifier;
        TextView seats;
        TextView date_time;

        LinearLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.reservation_fullname);
            identifier = itemView.findViewById(R.id.reservation_identifier);
            seats = itemView.findViewById(R.id.reservation_seats);
            date_time = itemView.findViewById(R.id.reservation_date_time);

        }
    }
}
