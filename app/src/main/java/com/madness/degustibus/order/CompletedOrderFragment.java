package com.madness.degustibus.order;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madness.degustibus.R;
import com.madness.degustibus.picker.DatePickerFragment;
import com.madness.degustibus.picker.TimePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedOrderFragment extends Fragment {

    private TextView totalPrice;
    private Button complete_btn;
    private ArrayList<Dish> dishList = new ArrayList<>();
    private Dish dish;
    private RecyclerView recyclerView;
    private SharedPreferences pref;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference databaseRef;
    private FirebaseUser user;
    private float totalAmount;
    private TextView totalAmountTextView;
    private TextView customerAddress;
    private DialogFragment timePicker;
    private DialogFragment datePicker;
    private TextView setDate;
    private TextView setTime;
    private String restaurantID;

    public CompletedOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_completed_order, container, false);
        getActivity().setTitle(getString(R.string.title_Complete));

        recyclerView = rootView.findViewById(R.id.recyclerViewOrderCompleted);
        complete_btn = rootView.findViewById(R.id.confirm_btn);
        totalAmountTextView = rootView.findViewById(R.id.total_price);
        customerAddress = rootView.findViewById(R.id.costumer_address);
        setDate = rootView.findViewById(R.id.setDate);
        setTime = rootView.findViewById(R.id.setTime);

        //set current time + 1 hour
        Date currentDate = Calendar.getInstance().getTime();        // get the current date time
        Calendar newCalendar = Calendar.getInstance();              // create a new calendar
        newCalendar.setTime(currentDate);                           // set it with the current date time
        newCalendar.add(Calendar.HOUR,1);                    // add one hour
        Date newDefaultDeliveryTime = newCalendar.getTime();        // create the default date time

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");   // date format
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");        // time format

        setDate.setText(dateFormat.format(newDefaultDeliveryTime));     // set Date TextView
        setTime.setText(timeFormat.format(newDefaultDeliveryTime));     // set Time TextView

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        //click on complete order create order and delete cart objects

        // configure date and time picker
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(),"datePicker");
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(),"timePicker");
            }
        });

        // submit operation
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalAmount == 0){
                    Toast.makeText(getContext(), getString(R.string.select), Toast.LENGTH_SHORT).show();
                }else{
                    boolean doOItOnce = true;
                    final ReservationClass reservation = new ReservationClass();
                    reservation.setCustomerAddress(customerAddress.getText().toString());
                    reservation.setCustomerID(user.getUid());
                    reservation.setDeliverymanID("null");
                    reservation.setDeliveryDate(setDate.getText().toString());
                    reservation.setDeliveryHour(setTime.getText().toString());
                    reservation.setTotalPrice(totalPrice.getText().toString());
                    reservation.setDescription(""); // fill it in the for
                    reservation.setStatus("new");

                    // store the selected dishes in the cart of the user
                    for(final Dish dish: dishList){             // for each dish in the dailyoffer
                        if(dish.getQuantity() > 0) {              // keep only the selected ones

                            if (doOItOnce){// retrieve restaurant address
                                restaurantID = dish.getRestaurant();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("restaurants")
                                        .child(dish.getRestaurant())
                                        .child("address").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        reservation.setRestaurantAddress(dataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                reservation.setRestaurantID(dish.getRestaurant());
                                doOItOnce = false;
                            }

                            FirebaseDatabase.getInstance().getReference()
                                    .child("offers")
                                    .child(dish.getRestaurant())
                                    .child(dish.identifier)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            System.out.println(dataSnapshot);
                                            if (dataSnapshot.exists()) {
                                                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                                String val = String.valueOf(Integer.parseInt(objectMap.get("avail").toString()) - dish.getQuantity());
                                                objectMap.put("avail", val);
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("offers")
                                                        .child(dish.getRestaurant())
                                                        .child(dish.identifier).updateChildren(objectMap);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            reservation.setDescription(reservation
                                    .getDescription()
                                    .concat(String.valueOf(dish.getQuantity())
                                            .concat("x ")
                                            .concat(dish.getDish())
                                            .concat("\n")
                                    ));
                        }
                    }

                    // store reservation
                    FirebaseDatabase.getInstance().getReference()
                            .child("orders")
                            .push().setValue(reservation);

                    sendNotification(restaurantID);

                    Toast.makeText(getContext(), getString(R.string.done), Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStackImmediate("HOME", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        //populate the list of cart objects
        loadFromFirebase();
        return rootView;
    }
    @Override
    public void onResume() {
        customerAddress = getView().findViewById(R.id.costumer_address);
        totalPrice = getView().findViewById(R.id.total_price);

        HashMap<String, Object> userData = (HashMap<String, Object>) getArguments().getSerializable("user");
        customerAddress.setText(userData.get("address").toString());
        totalPrice.setText(pref.getString("totPrice","20.5"));
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }

    public void loadFromFirebase(){

        // obtain the url /offers/{restaurantIdentifier}
        databaseRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("customers")
                .child(user.getUid())
                .child("cart");

        Query query = databaseRef; // query data at /customers/{customerIdentifier}/cart

        FirebaseRecyclerOptions<Dish> options =
                new FirebaseRecyclerOptions.Builder<Dish>()
                        .setQuery(query, new SnapshotParser<Dish>() {
                            @NonNull
                            @Override
                            public Dish parseSnapshot(@NonNull DataSnapshot snapshot) {
                                dish = snapshot.getValue(Dish.class);  // get the snapshot and cast it
                                // into a `Dish` item
                                dishList.add(dish);                         // add the `dish` into the list
                                return dish;                               // return the item to the builder
                            }
                        }).build(); // build the option


        adapter = new FirebaseRecyclerAdapter<Dish, CartHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartHolder holder, final int position, @NonNull final Dish model) {
                // the user can only decrease the selected quantity
                final int maxQuantity = model.getQuantity();

                holder.title.setText(model.getDish());
                holder.price.setText(model.getPrice() + " €");
                holder.quantity.setText(String.valueOf(model.getQuantity()));
                totalAmount += Float.parseFloat(model.price) * model.quantity;
                totalAmountTextView.setText(String.valueOf(totalAmount));

                // set button plus
                holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n =Integer.parseInt(holder.quantity.getText().toString());
                        // check for the availability of the product
                        if(n < maxQuantity){
                            n ++;
                            holder.quantity.setText(String.valueOf(n));
                            dishList.get(position).setQuantity(n);
                            totalAmount += Float.parseFloat(model.getPrice());
                            totalAmountTextView.setText(String.valueOf(totalAmount));
                        }

                    }
                });

                // set button minus
                holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n =Integer.parseInt(holder.quantity.getText().toString());
                        // check for non negative numbers
                        if(n > 0){
                            n --;
                            holder.quantity.setText(String.valueOf(n));
                            dishList.get(position).setQuantity(n);
                            totalAmount -= Float.parseFloat(model.getPrice());
                            totalAmountTextView.setText(String.valueOf(totalAmount));
                        }
                    }
                });
            }

            @Override
            public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_listitem, parent, false);
                return new CartHolder(view);

            }

        };
        recyclerView.setAdapter(adapter);
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

    public void setDeliveryDate(int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + month + "/" + year;
        setDate.setText(date);
    }

    public void setDeliveryTime(int hourOfDay, int minute) {
        String date = hourOfDay + ":" + minute;
        setTime.setText(date);
    }

    private void sendNotification(final String restaurantID) {
        /* Send notification to user */
        final Map<String, Object> newNotification = new HashMap<String, Object>();
        newNotification.put("type", getString(R.string.typeNot_new));
        newNotification.put("description", getString(R.string.desc));

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        newNotification.put("date", dateFormat.format(date));

        FirebaseDatabase.getInstance().getReference().child("notifications").child(restaurantID).push().setValue(newNotification);
    }
}