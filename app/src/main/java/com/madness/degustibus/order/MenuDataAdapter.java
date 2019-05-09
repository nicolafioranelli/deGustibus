package com.madness.degustibus.order;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.R;

import java.util.ArrayList;

public class MenuDataAdapter extends RecyclerView.Adapter<MenuDataAdapter.MenuViewHolder> {

    private ArrayList<Dish> dishList;


    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public MenuDataAdapter(ArrayList<Dish> dishes) {
        this.dishList = dishes;
    }

    @NonNull
    @Override
    public MenuDataAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_listitem, parent, false);


        return new MenuDataAdapter.MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuDataAdapter.MenuViewHolder holder, int position) {
        Dish menu = dishList.get(position);
        holder.title.setText(menu.getDish());
        holder.description.setText(menu.getDesc());
        holder.price.setText(menu.getPrice());
        holder.quantity.setText(menu.getAvail());
        if (menu.getPic() == null) {
            // Set default image
            holder.image.setImageResource(R.drawable.dish_image);
        } else {
            holder.image.setImageURI(Uri.parse(menu.getPic()));
        }
    }

    @Override
    public int getItemCount() {
        return dishList == null ? 0 : dishList.size();
    }

    public void remove(int position) {
        dishList.remove(position);
        notifyItemRemoved(position);
    }

    public Dish getMenuClass(int position) {
        return dishList.get(position);
    }

    public Dish getDish(int position) {
        return dishList.get(position);
    }

    public void add(int position, Dish menuClass) {
        dishList.add(position, menuClass);
        notifyItemInserted(position);
    }

    public ArrayList<Dish> getList() {
        return dishList;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        private TextView title, description, price, quantity;
        private ImageView image;
        private Button buttonPlus;
        private Button buttonMinus;

        public MenuViewHolder (View view) {

            super(view);
            title = view.findViewById(R.id.rest_title);
            description = view.findViewById(R.id.rest_description);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            image = view.findViewById(R.id.rest_imageView);
            buttonMinus = view.findViewById(R.id.buttonPlus);

            if (Integer.parseInt(quantity.getText().toString())==0){
                buttonMinus.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
            }
            buttonMinus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    Dish dish = getDish(clickPosition);
                    if (v.getId() == R.id.buttonPlus) {
                        if(Integer.parseInt(quantity.getText().toString())!= 0){
                            int n =Integer.parseInt(quantity.getText().toString());
                            n --;
                            quantity.setText(String.valueOf(n));
                            dish.setAvail(String.valueOf(n));
                        }
                    }
                }
            });
            buttonPlus = view.findViewById(R.id.buttonMinus);
            buttonPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    Dish dish = getDish(clickPosition);
                    if (v.getId() == R.id.buttonMinus) {
                        int n =Integer.parseInt(quantity.getText().toString());
                        n ++;
                        quantity.setText(String.valueOf(n));
                        dish.setAvail(String.valueOf(n));
                    }
                }
            });
        }
    }
}