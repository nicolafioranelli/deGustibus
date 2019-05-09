package com.madness.degustibus.order;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madness.degustibus.R;

import java.util.List;

public class CartHolder extends RecyclerView.ViewHolder {

    private static final String TAG = CartHolder.class.getSimpleName();
    public TextView title, price, quantity;
    public Button buttonPlus;
    public Button buttonMinus;

    private List<CartClass> cart_offers;

    public CartClass getDish(int position) {
        return cart_offers.get(position);
    }
    public void remove(int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart/" + cart_offers.get(position).getId());
        databaseRef.removeValue();
    }

    public CartHolder(final View view) {
        super(view);
        title = view.findViewById(R.id.rest_title);
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