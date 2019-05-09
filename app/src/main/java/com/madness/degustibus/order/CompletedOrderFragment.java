package com.madness.degustibus.order;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.DatePickerFragment;
import com.madness.degustibus.R;
import com.madness.degustibus.TimePickerFragment;
import com.madness.degustibus.home.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedOrderFragment extends Fragment {
    private TextView day;
    private TextView time;
    LinearLayout lunchday;
    LinearLayout lunchtime;
    private TextView customerAddress;
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
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;


    public CompletedOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_completed_order, container, false);
        getActivity().setTitle("Complete order");

        recyclerView = rootView.findViewById(R.id.recyclerViewOrderCompleted);
        complete_btn = rootView.findViewById(R.id.confirm_btn);
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //click on complete order create order and delete cart objects
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
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                final DatabaseReference dbRef = db.getReference("orders");
                for(CartClass d: dishList){
                    descr = descr+ " - " +d.getQuantity() + " x " + d.getTitle() ;

                }
                order.put("title","order");
                order.put("idRest",getArguments().getString("restId"));
                order.put("RestName",getArguments().getString("restName"));
                order.put("RestAddress",getArguments().getString("restAddress"));
                order.put("description",descr);
                order.put("CostumerName",pref.getString("name", getResources().getString(R.string.frProfile_defName)));
                order.put("address",customerAddress.getText().toString());
                order.put("CostumerPhone",pref.getString("phone", getResources().getString(R.string.frProfile_defPhone)));
                order.put("Idcustomer",FirebaseAuth.getInstance().getCurrentUser().getUid());
                order.put("date",day.getText().toString());
                order.put("hour",time.getText().toString());
                order.put("priceProd",String.valueOf(prodPrice));
                order.put("priceShip",String.valueOf(shipPrice));
                order.put("price",String.valueOf((prodPrice)+shipPrice));
                order.put("state","Incoming");
                dbRef.push().setValue(order);
                databaseRef.child("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart").removeValue();
                Toast.makeText(getContext(), "ToDo: Completed!", Toast.LENGTH_SHORT).show();
            }
        });
        //populate the list of cart objects
        populateList();
        return rootView;
    }
    @Override
    public void onResume() {
        customerAddress = getView().findViewById(R.id.costumer_address);
        productsPrice = getView().findViewById(R.id.products_price);
        shippingPrice = getView().findViewById(R.id.shipping_price);
        totalPrice = getView().findViewById(R.id.total_price);

        customerAddress.setText(pref.getString("address", getResources().getString(R.string.frProfile_defAddr)));
        productsPrice.setText(pref.getString("prodPrice","18"));
        shippingPrice.setText(pref.getString("shipPrice","2.5"));
        totalPrice.setText(pref.getString("totPrice","20.5"));

        getDayAndTime();

        super.onResume();
    }

    public void getDayAndTime() {
        lunchday =getActivity().findViewById(R.id.lunchDayLinearLayout);
        lunchday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker= new DatePickerFragment();
                datePicker.show(getFragmentManager(), "date picker");
            }
        });
        lunchtime =getActivity().findViewById(R.id.lunchTimeLinearLayout);
        lunchtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setCartDataAdapter();
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        day = getActivity().findViewById(R.id.et_edit_lunchday);
        time = getActivity().findViewById(R.id.et_edit_lunchtime);
        if(savedInstanceState!=null){
            loadBundle(savedInstanceState);
        }else{
            loadSharedPrefs();
        }
    }

    private void loadBundle(Bundle bundle){
        time.setText(bundle.getString("reservationTime"));
        day.setText(bundle.getString("reservationDay"));

    }


    private void loadSharedPrefs(){
        time.setText(pref.getString("reservationTime", "13:00"));
        day.setText(pref.getString("reservationDay","01/01/2019"));
    }


    public void setHourAndMinute(int hour, int minute) {
        time.setText(String.format("%02d:%02d", hour, minute));
    }

    public void setDate(int year, int month, int day){
        this.day.setText(String.format("%2d/%2d/%4d",day,month, year));
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("reservationTime",time.getText().toString());
        outState.putString("reservationDay", day.getText().toString());
    }


    private void setCartDataAdapter() {
        mAdapter = new CartDataAdapter(dishList);
    }

    void populateList(){
         DatabaseReference dbR = FirebaseDatabase.getInstance().getReference("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");
        Query query = FirebaseDatabase.getInstance().getReference().child("customers/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/cart");

        FirebaseRecyclerOptions<CartClass> options =
                new FirebaseRecyclerOptions.Builder<CartClass>()
                        .setQuery(query, CartClass.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<CartClass, CartHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartHolder holder, int position, @NonNull CartClass model) {
                CartClass menu = dishList.get(position);
                holder.title.setText(menu.getTitle());
                holder.price.setText(menu.getPrice());
                holder.quantity.setText(menu.getQuantity());

            }

            @Override
            public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_listitem, parent, false);
                return new CartHolder(view);

            }

        };
        recyclerView.setAdapter(adapter);
        dbR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again whenever data at this location is updated
                for(DataSnapshot dS : dataSnapshot.getChildren()){
                    dish = new CartClass(dS.getValue(CartClass.class).getTitle(),dS.getValue(CartClass.class).getPrice(),dS.getValue(CartClass.class).getQuantity());
                    dish.setId(dS.getKey());
                    prodPrice = prodPrice + (Double.parseDouble(dish.getPrice())*Integer.parseInt(dish.getQuantity()));
                    dishList.add(dish);
                    productsPrice.setText(String.valueOf(prodPrice));
                    totalPrice.setText(String.valueOf(prodPrice+shipPrice));
                }



                mAdapter = new CartDataAdapter(dishList);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

}
