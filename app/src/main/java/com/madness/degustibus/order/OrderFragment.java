package com.madness.degustibus.order;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
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
import com.madness.degustibus.GlideApp;
import com.madness.degustibus.R;
import com.madness.degustibus.picker.DatePickerFragment;
import com.madness.degustibus.picker.TimePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderFragment extends Fragment {

    /* Firebase */
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseRecyclerAdapter adapter;
    private ValueEventListener eventListener;
    private DatabaseReference listenerReference;

    /* Widgets */
    private TextView name;
    private TextView address;
    private RatingBar ratingBar;
    private TextView customerAddress;
    private TextView totalPrice;
    private RecyclerView recyclerView;
    private TextView setDate;
    private TextView setTime;
    private DialogFragment timePicker;
    private DialogFragment datePicker;
    private Button complete_btn;

    /* Data */
    private float totalAmount;
    private JSONObject restaurant;
    private String restaurantID;
    private ProfileClass userProfile;
    private MenuItem item;
    private ArrayList<MenuItem> menu;
    private boolean order = true;

    public OrderFragment() {
        // Required empty public constructor
    }

    /* Lifecycle */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        menu = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle(getString(R.string.title_Order));
        name = rootView.findViewById(R.id.rest_title);
        address = rootView.findViewById(R.id.rest_subtitle);
        ratingBar = rootView.findViewById(R.id.ratingBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        customerAddress = rootView.findViewById(R.id.costumer_address);
        totalPrice = rootView.findViewById(R.id.total_price);
        setDate = rootView.findViewById(R.id.setDate);
        setTime = rootView.findViewById(R.id.setTime);
        complete_btn = rootView.findViewById(R.id.confirm_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadUserData();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String restaurantProfileString = getArguments().getString("restaurant");
        try {
            restaurant = new JSONObject(restaurantProfileString);
            name.setText(restaurant.get("name").toString());
            address.setText(restaurant.get("address").toString());
            ratingBar.setRating(Float.valueOf(restaurant.get("rating").toString()));
            restaurantID = restaurant.get("id").toString();
        } catch (JSONException e) {

        }

        //set current time + 1 hour
        Date currentDate = Calendar.getInstance().getTime();        // get the current date time
        Calendar newCalendar = Calendar.getInstance();              // create a new calendar
        newCalendar.setTime(currentDate);                           // set it with the current date time
        newCalendar.add(Calendar.HOUR, 1);                  // add one hour
        Date newDefaultDeliveryTime = newCalendar.getTime();        // create the default date time

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");   // date format
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");        // time format

        setDate.setText(dateFormat.format(newDefaultDeliveryTime));     // set Date TextView
        setTime.setText(timeFormat.format(newDefaultDeliveryTime));     // set Time TextView

        // configure date and time picker
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });

        loadMenu();

        // submit operation
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalAmount == 0) {
                    Toast.makeText(getContext(), getString(R.string.select), Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> cart = new HashMap<>();
                    ReservationClass reservation = new ReservationClass(
                            user.getUid(),
                            restaurantID,
                            "null",
                            setDate.getText().toString(),
                            setTime.getText().toString(),
                            totalPrice.getText().toString(),
                            userProfile.getAddress(),
                            address.getText().toString(),
                            "new",
                            "null",
                            "null",
                            "null",
                            "null"
                    );

                    for (MenuItem item : menu) {
                        if (item.getQuantity() > 0) {
                            HashMap<String, Object> dish = new HashMap<>();
                            dish.put("name", item.getDish());
                            dish.put("quantity", item.getQuantity());
                            dish.put("rating", item.getRating());
                            cart.put(item.getId(), dish); // <id_plate, data>
                            databaseReference.child("offers").child(restaurantID).child(item.getId()).child("popular").setValue(item.getPopular() + 1);
                            databaseReference.child("offers").child(restaurantID).child(item.getId()).child("avail").setValue(item.getAvail() - item.getQuantity());
                        }
                    }

                    DatabaseReference df = databaseReference.child("orders").push();
                    df.setValue(reservation);
                    df.child("cart").updateChildren(cart);

                    increaseRestAndNotify();

                    //Toast.makeText(getContext(), getString(R.string.done), Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.orderTitle))
                            .setMessage(getString(R.string.orderDesc))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })

                            .show();

                    performNoBackStackTransaction();
                }
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
        try {
            adapter.stopListening();
            listenerReference.removeEventListener(eventListener);
        } catch (Exception e) {

        }
    }
    // end Lifecycle

    /* Helpers */
    private void loadMenu() {
        Query query = databaseReference.child("offers").child(restaurantID).orderByChild("popular");
        FirebaseRecyclerOptions<MenuClass> options =
                new FirebaseRecyclerOptions.Builder<MenuClass>()
                        .setQuery(query, new SnapshotParser<MenuClass>() {
                            @NonNull
                            @Override
                            public MenuClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                MenuClass elem = snapshot.getValue(MenuClass.class);
                                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                                item = new MenuItem(0, snapshot.getKey(), map.get("dish").toString(), 0, Integer.parseInt(map.get("popular").toString()), Integer.parseInt(map.get("avail").toString()));
                                menu.add(item);
                                return elem;
                            }
                        })
                        .build();


        adapter = new FirebaseRecyclerAdapter<MenuClass, MenuHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuHolder holder, final int position, @NonNull final MenuClass model) {
                holder.title.setText(model.getDish());
                holder.description.setText(model.getDescription());
                holder.price.setText(model.getPrice().toString() + " €");
                GlideApp.with(getContext())
                        .load(model.getPic())
                        .placeholder(R.drawable.restaurant)
                        .into(holder.imageView);


                final int maxQuantity = model.getAvail();
                totalAmount += model.getPrice() * Integer.parseInt(holder.quantity.getText().toString());
                totalPrice.setText(String.valueOf(totalAmount));

                // set button plus
                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (order) {
                            Collections.reverse(menu);
                            order = false;
                        }
                        int n = Integer.parseInt(holder.quantity.getText().toString());
                        // check for the availability of the product
                        if (n < maxQuantity) {
                            n++;
                            holder.quantity.setText(String.valueOf(n));
                            menu.get(position).setQuantity(n);
                            totalAmount += model.getPrice();
                            totalPrice.setText(String.format("%.2f", totalAmount));
                        }

                    }
                });

                // set button minus
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (order) {
                            Collections.reverse(menu);
                            order = false;
                        }
                        int n = Integer.parseInt(holder.quantity.getText().toString());
                        // check for non negative numbers
                        if (n > 0) {
                            n--;
                            holder.quantity.setText(String.valueOf(n));
                            menu.get(position).setQuantity(n);
                            totalAmount -= model.getPrice();
                            totalPrice.setText(String.format("%.2f", totalAmount));
                        }
                    }
                });
            }

            @NonNull
            @Override
            public MenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.menu_item, viewGroup, false);
                return new MenuHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadUserData() {
        listenerReference = databaseReference.child("customers").child(user.getUid());
        eventListener = listenerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(ProfileClass.class);
                customerAddress.setText(userProfile.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void increaseRestAndNotify() {
        final DatabaseReference ref = databaseReference.child("restaurants").child(restaurantID).child("popular");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                ref.setValue(Integer.parseInt(value.toString()) + 1);

                try {
                    Client client = new Client("LRBUKD1XJR", "d796532dfd54cafdf4587b412ad560f8");
                    Index index = client.getIndex("rest_HOME");
                    JSONObject object = new JSONObject()
                            .put("popular", (Integer.parseInt(value.toString()) + 1));

                    index.partialUpdateObjectAsync(object, restaurantID, null);
                } catch (JSONException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* Send notification to user */
        final Map<String, Object> newNotification = new HashMap<String, Object>();
        newNotification.put("type", getString(R.string.typeNot_new));
        newNotification.put("description", getString(R.string.desc));

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        newNotification.put("date", dateFormat.format(date));

        FirebaseDatabase.getInstance().getReference().child("notifications").child(restaurantID).push().setValue(newNotification);
    }

    public void performNoBackStackTransaction() {
        final FragmentManager fragmentManager = getFragmentManager();

        try {
            Fragment fragment = null;
            Class fragmentClass;
            fragmentClass = com.madness.degustibus.home.HomeFragment.class;
            fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "HOME").commit();
        } catch (Exception e) {
            Log.e("MAD", "onCreate: ", e);
        }
    }

    public void setDeliveryDate(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, month);
        String format = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        setDate.setText(format);
    }

    public void setDeliveryTime(int hourOfDay, int minute) {
        setTime.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
    // end Helpers
}
