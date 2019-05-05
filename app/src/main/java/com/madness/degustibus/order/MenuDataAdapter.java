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
import android.widget.Toast;

import com.madness.degustibus.R;

import java.util.ArrayList;
import java.util.Map;

public class MenuDataAdapter extends RecyclerView.Adapter<MenuDataAdapter.MenuViewHolder> {

    private ArrayList<MenuClass> dishList;


    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public MenuDataAdapter(ArrayList<MenuClass> dishes) {
        this.dishList = dishes;
    }

    @NonNull
    @Override
    public MenuDataAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_listitem, parent, false);


        return new MenuDataAdapter.MenuViewHolder(itemView);
    }
    public void onClick(View v) {
        if(v.getId() == R.id.buttonMinus) {


        }
        if(v.getId() == R.id.buttonPlus) {


        }
    }

    @Override
    public void onBindViewHolder(@NonNull MenuDataAdapter.MenuViewHolder holder, int position) {
        MenuClass menu = dishList.get(position);
        holder.title.setText(menu.getTitle());
        holder.description.setText(menu.getDescription());
        holder.price.setText(menu.getPrice());
        holder.quantity.setText(menu.getQuantity());
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

    public MenuClass getMenuClass(int position) {
        return dishList.get(position);
    }

    public MenuClass getDish(int position) {
        return dishList.get(position);
    }

    public void add(int position, MenuClass menuClass) {
        dishList.add(position, menuClass);
        notifyItemInserted(position);
    }

    public ArrayList<MenuClass> getList() {
        return dishList;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        private TextView title, description, price, quantity;
        private ImageView image;
        private Button buttonPlus;
        private Button buttonMinus;

        public MenuViewHolder (View view) {

            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            image = view.findViewById(R.id.imageView);
            buttonMinus = view.findViewById(R.id.buttonMinus);

            if (Integer.parseInt(quantity.getText().toString())==0){
                buttonMinus.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
            }
            buttonMinus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    MenuClass dish = getDish(clickPosition);
                    if (v.getId() == R.id.buttonMinus) {
                            int n =Integer.parseInt(quantity.getText().toString());
                            n --;
                            quantity.setText(String.valueOf(n));
                            dish.setQuantity(String.valueOf(n));

                    }
                }
            });
            buttonPlus = view.findViewById(R.id.buttonPlus);
            buttonPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    MenuClass dish = getDish(clickPosition);
                    if (v.getId() == R.id.buttonPlus) {
                            int n =Integer.parseInt(quantity.getText().toString());
                            n ++;
                            quantity.setText(String.valueOf(n));
                            dish.setQuantity(String.valueOf(n));
                    }
                }
            });
        }
    }
}
