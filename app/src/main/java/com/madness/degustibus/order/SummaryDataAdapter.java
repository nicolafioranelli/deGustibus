package com.madness.degustibus.order;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madness.degustibus.R;

import java.util.ArrayList;

public class SummaryDataAdapter extends RecyclerView.Adapter<SummaryDataAdapter.MenuViewHolder> {

    private ArrayList<CartClass> dishList;


    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public SummaryDataAdapter(ArrayList<CartClass> dishes) {

        this.dishList = dishes;
    }

    @NonNull
    @Override
    public SummaryDataAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_listitem, parent, false);


        return new SummaryDataAdapter.MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryDataAdapter.MenuViewHolder holder, int position) {
        CartClass menu = dishList.get(position);
        holder.title.setText(menu.getTitle());
        holder.price.setText(menu.getPrice());
        holder.quantity.setText(menu.getQuantity());
    }

    @Override
    public int getItemCount() {
        return dishList == null ? 0 : dishList.size();
    }

    public void remove(int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart/" + dishList.get(position).getId());
        databaseRef.removeValue();
        //dishList.remove(position);
        //notifyItemRemoved(position);
        dishList.clear();
    }

    public CartClass getCartClass(int position) {
        return dishList.get(position);
    }

    public CartClass getDish(int position) {
        return dishList.get(position);
    }

    public void add(int position, CartClass cartClass) {
        dishList.add(position, cartClass);
        notifyItemInserted(position);
    }

    public ArrayList<CartClass> getList() {
        return dishList;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        private TextView title, price, quantity;
        private Button buttonPlus;
        private Button buttonMinus;

        public MenuViewHolder (View view) {

            super(view);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            quantity = view.findViewById(R.id.quantity);
            buttonMinus = view.findViewById(R.id.buttonMinus);

            if (Integer.parseInt(quantity.getText().toString())==0){
                buttonMinus.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
            }
            buttonMinus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    CartClass dish = getDish(clickPosition);
                    if (v.getId() == R.id.buttonMinus) {
                        int n =Integer.parseInt(quantity.getText().toString());
                        n --;
                        quantity.setText(String.valueOf(n));
                        dish.setQuantity(String.valueOf(n));
                        if((Integer.parseInt(quantity.getText().toString())+1)==1){

                            remove(clickPosition);
                        }
                    }
                }
            });
            buttonPlus = view.findViewById(R.id.buttonPlus);
            buttonPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int clickPosition = getAdapterPosition();
                    CartClass dish = getDish(clickPosition);
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
