package com.madness.degustibus.order;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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
public class SummaryOrderFragment extends Fragment {
    private TextView tv;
    private TextView productsPrice;
    private TextView shippingPrice;
    private TextView totalPrice;
    private Button complete_btn;
    private Fragment fragment;
    ArrayList<CartClass> dishList = new ArrayList<>();
    HashMap<String,String> order=new HashMap<>();
    CartClass dish;
    String descr = "";
    double prodPrice= 0.0;
    double shipPrice= 2.5;
    double totPrice = 0.0;
    private RecyclerView recyclerView;
    private CartDataAdapter mAdapter;
    private SharedPreferences pref;


    public SummaryOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.summary_order, container, false);
        getActivity().setTitle("Order");
        final String idOrd = this.getArguments().getString("idOrder");



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("orders");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        //populate the list of cart objects
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                  if(dS.getKey()==idOrd){
                      for(DataSnapshot d: dS.getChildren()){
                          if(d.getKey().equals("address") ) {
                              tv = getView().findViewById(R.id.summary_costumer_addr);

                          }
                          else if(d.getKey().equals("description")) {
                              tv = getView().findViewById(R.id.summary_descr);
                          }
                          else if(d.getKey().equals("hour")) {
                              tv = getView().findViewById(R.id.summary_hour);
                          }
                          else if(d.getKey().equals("date")) {
                              tv = getView().findViewById(R.id.summary_data);
                          }
                          else if(d.getKey().equals("price")) {
                              tv = getView().findViewById(R.id.summary_total_price);
                          }
                          else if(d.getKey().equals("priceProd")) {
                              tv = getView().findViewById(R.id.summary_products_price);
                          }
                          else if(d.getKey().equals("priceShip")) {
                              tv = getView().findViewById(R.id.summary_shipping_price);
                          }
                          else if(d.getKey().equals("state")) {
                              tv = getView().findViewById(R.id.order_state);
                          }
                          else continue;
                          tv.setText( d.getValue().toString());

                      }
                  }
                }
                tv=rootView.findViewById(R.id.order_id);
                tv.setText(idOrd);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }
    @Override
    public void onResume() {
        /*customerAddress = getView().findViewById(R.id.costumer_address);
        productsPrice = getView().findViewById(R.id.products_price);
        shippingPrice = getView().findViewById(R.id.shipping_price);
        totalPrice = getView().findViewById(R.id.total_price);

        customerAddress.setText(pref.getString("addressCustomer", getResources().getString(R.string.es_street)));
        productsPrice.setText(pref.getString("prodPrice","18"));
        shippingPrice.setText(pref.getString("shipPrice","2.5"));
        totalPrice.setText(pref.getString("totPrice","20.5"));*/

        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setCartDataAdapter();
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }

    private void setCartDataAdapter() {
        mAdapter = new CartDataAdapter(dishList);
    }

}
