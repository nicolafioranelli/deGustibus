package com.madness.restaurant.daily;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madness.restaurant.R;
import com.madness.restaurant.swipe.SwipeController;
import com.madness.restaurant.swipe.SwipeControllerActions;

public class DailyFragment2 extends Fragment {
    private int replaced = 0;
    private int addedposition = 0;
    private boolean added = true;
    private int mColumnCount = 1;
    private DailyFragment.DailyListener listener;
    private SwipeController swipeController;
    private RecyclerView recyclerView;
    private DatabaseReference mDataRef;
    private FirebaseRecyclerAdapter<DailyClass, ViewHolder> adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DailyFragment.DailyListener) {
            listener = (DailyFragment.DailyListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DailyListner");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dailyoffers, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Daily)+ " 2");

        recyclerView = rootView.findViewById(R.id.dishes);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        mDataRef = FirebaseDatabase.getInstance().getReference().child("Offers");

        FirebaseRecyclerOptions<DailyClass> options = new FirebaseRecyclerOptions.Builder<DailyClass>()
                .setQuery(mDataRef, DailyClass.class).build();

        adapter = new FirebaseRecyclerAdapter<DailyClass, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DailyClass model) {

                //String imgUrl = model.getImage();
                //Picasso.get().load(imgUrl).into(holder.post_image);
                holder.dish.setText(model.getDish());

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailyoffer_listitem, parent, false);
                ViewHolder holder = new ViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);

        /*
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // set swipe controller
        swipeController = new SwipeController((new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                added = false;
                replaced = position + 1;
                editor.putString("dish", adapter.getDailyClass(position).getDish());
                editor.putString("descDish", adapter.getDailyClass(position).getType());
                editor.putString("avail", adapter.getDailyClass(position).getAvail());
                editor.putString("price", adapter.getDailyClass(position).getPrice());
                editor.putString("photoDish", adapter.getDailyClass(position).getPic());
                editor.apply();
                listener.addDailyOffer();
                //Log.d("MAD", "onLeftClicked: left");
                super.onLeftClicked(position);
            }

            @Override
            public void onRightClicked(int position) {
                adapter.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                //Log.d("MAD", "onLeftClicked: right");
                super.onRightClicked(position);
            }
        }), this.getContext());
        */
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dish, type, avail, price;
        ImageView pic;

        ViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.dish_icon);
            dish = itemView.findViewById(R.id.dish_name);
            type = itemView.findViewById(R.id.dish_type);
            avail = itemView.findViewById(R.id.dish_quantity);
            price = itemView.findViewById(R.id.dish_price);
        }
    }
}
