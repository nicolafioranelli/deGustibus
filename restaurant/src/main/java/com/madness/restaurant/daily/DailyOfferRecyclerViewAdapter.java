package com.madness.restaurant.daily;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madness.restaurant.R;

import java.util.ArrayList;

public class DailyOfferRecyclerViewAdapter extends RecyclerView.Adapter<DailyOfferRecyclerViewAdapter.ViewHolder> {

    private Context _context;
    private ArrayList<String> _dishNames = null;
    private ArrayList<Integer> _dishPics =  null;
    private ArrayList<String> _quantities = null;
    private ArrayList<String> _types = null;
    private ArrayList<String> _prices = null;

    public DailyOfferRecyclerViewAdapter(Context _context, ArrayList<String> _dishNames, ArrayList<Integer> _dishPics, ArrayList<String> _quantities, ArrayList<String> _types, ArrayList<String> _prices) {
        this._context = _context;
        this._dishNames = _dishNames;
        this._dishPics = _dishPics;
        this._types = _types;
        this._quantities = _quantities;
        this._prices = _prices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailyoffer_listitem, parent, false); // TODO check layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        holder.dish_name.setText(_dishNames.get(i));
        //holder.dish_pic.setImageDrawable(_dishPics.get(i));
        holder.type.setText(_types.get(i));
        holder.quantity.setText(_quantities.get(i));
        holder.price.setText(_prices.get(i));
    }

    @Override
    public int getItemCount() {
        return _dishNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView dish_name;
        TextView type;
        TextView quantity;
        TextView price;
        ImageView dish_pic;

        LinearLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dish_pic = itemView.findViewById(R.id.dish_icon);
            dish_name = itemView.findViewById(R.id.dish_name);
            type = itemView.findViewById(R.id.dish_type);
            quantity = itemView.findViewById(R.id.dish_quantity);
            price = itemView.findViewById(R.id.dish_quantity);

        }
    }

}
