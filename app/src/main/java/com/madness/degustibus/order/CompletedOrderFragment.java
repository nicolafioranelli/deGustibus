package com.madness.degustibus.order;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.R;
import com.madness.degustibus.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedOrderFragment extends Fragment {
    private TextView customerAddress;
    private TextView productsPrice;
    private TextView shippingPrice;
    private TextView totalPrice;
    private Button complete_btn;
    private Fragment fragment;
    ArrayList<CartClass> dishList = new ArrayList<>();
    HashMap<String,String> orders;
    CartClass dish;
    private RecyclerView recyclerView;
    private CartDataAdapter mAdapter;
    private SharedPreferences pref;


    public CompletedOrderFragment() {
        // Required empty public constructor
        /*fakeConstructor();*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_completed_order, container, false);
        getActivity().setTitle("Complete order");

        recyclerView = rootView.findViewById(R.id.recyclerViewOrderCompleted);
        complete_btn = rootView.findViewById(R.id.confirm_btn);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragment = null;
                    Class fragmentClass;
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    Log.e("MAD", "editProfileClick: ", e);
                }

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, fragment, getString(R.string.title_Home))
                        .addToBackStack("Home")
                        .commit();
                Toast.makeText(getContext(), "ToDo: Completed!", Toast.LENGTH_SHORT).show();
            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                /*System.out.println("inizio" );
                orders= (HashMap<String, String>) dataSnapshot.getValue();
                System.out.println("dimmi" + orders.values());
                System.out.println("prova" + orders.);*/
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    System.out.println("prova" + dS + "-" + dS.getValue().getClass());
                    dish = new CartClass(dS.getValue(CartClass.class).getTitle(),dS.getValue(CartClass.class).getPrice(),dS.getValue(CartClass.class).getQuantity());
                    dishList.add(dish);
                }

                mAdapter = new CartDataAdapter(dishList);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }
    @Override
    public void onResume() {
        customerAddress = getView().findViewById(R.id.costumer_address);
        productsPrice = getView().findViewById(R.id.products_price);
        shippingPrice = getView().findViewById(R.id.shipping_price);
        totalPrice = getView().findViewById(R.id.total_price);

        customerAddress.setText(pref.getString("addressCustomer", getResources().getString(R.string.es_street)));
        productsPrice.setText(pref.getString("prodPrice","18"));
        shippingPrice.setText(pref.getString("shipPrice","2.5"));
        totalPrice.setText(pref.getString("totPrice","20.5"));

        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setCartDataAdapter();
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }

    /*private void fakeConstructor() {
        menuClass dish = new MenuClass("Pizza Margherita", "Base impasto integrale, pomodoro, mozzarella, basilico", "3.50 €", "2", null);
        this.dishList.add(dish);
        dish = new MenuClass("Pizza diavola", "Base impasto integrale, pomodoro, mozzarella, basilico", "5.50 €", "1", null);
        this.dishList.add(dish);
        dish = new MenuClass("Pizza patatine", "Base impasto integrale, pomodoro, mozzarella, basilico", "5.50 €", "1", null);
        this.dishList.add(dish);

    }*/

    private void setCartDataAdapter() {
        mAdapter = new CartDataAdapter(dishList);
    }

}
