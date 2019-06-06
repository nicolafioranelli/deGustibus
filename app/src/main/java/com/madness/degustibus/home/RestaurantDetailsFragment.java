package com.madness.degustibus.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantDetailsFragment extends Fragment {

    /* Firebase */
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseRecyclerAdapter adapter;

    /* Widgets */
    private ImageView imageView;
    private TextView title;
    private TextView description;
    private TextView address;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private Button button;
    private MenuInflater menuInflaterP;
    private Menu menuP;

    /* Data */
    private JSONObject restaurant;
    private DetailsInterface detailsInterface;
    private String restID;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    /* Lifecycle */
    /* The onAttach method registers the DetailsInterface */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsInterface) {
            detailsInterface = (DetailsInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement DetailsInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        getActivity().setTitle(getString(R.string.title_Restaurant));
        imageView = rootView.findViewById(R.id.rest_imageView);
        title = rootView.findViewById(R.id.rest_title);
        address = rootView.findViewById(R.id.rest_subtitle);
        description = rootView.findViewById(R.id.rest_description);
        ratingBar = rootView.findViewById(R.id.ratingBar);
        button = rootView.findViewById(R.id.orderButton);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String restaurantProfileString = getArguments().getString("restaurant");
        populate(restaurantProfileString);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsInterface.newRestaurantOrder(restaurant.toString());
            }
        });
    }
    // end Lifecycle

    /* Option menu Helpers */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuP = menu;
        menuInflaterP = inflater;

        // according to value received is shown the full star or the border star (preferred/not)
        if (getArguments().getBoolean("isPreferred")) {
            inflater.inflate(R.menu.preferred, menu);
        } else {
            inflater.inflate(R.menu.not_preferred, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Restaurants", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            restaurant = new JSONObject(getArguments().getString("restaurant"));
            menuP.clear(); //clear menu and reload it
            if (item.getItemId() == R.id.action_notpreferred) {
                editor.putString(restaurant.get("name").toString(), restaurant.getString("id"));
                editor.commit();
                menuInflaterP.inflate(R.menu.preferred, menuP);
            }
            if (item.getItemId() == R.id.action_preferred) {
                editor.remove(restaurant.get("name").toString());
                editor.commit();
                menuInflaterP.inflate(R.menu.not_preferred, menuP);
            }
        } catch (JSONException e) {

        }
        return super.onOptionsItemSelected(item);
    }

    /* Populate the view with firebase data */
    private void populate(String restaurantProfileString) {
        try {
            restaurant = new JSONObject(restaurantProfileString);
            String photo = null;

            if(restaurant.has("photo")) {
                photo = restaurant.get("photo").toString();
            }

            GlideApp.with(getContext())
                    .load(photo)
                    .placeholder(R.drawable.restaurant)
                    .into(imageView);

            title.setText(restaurant.get("name").toString());
            address.setText(restaurant.get("address").toString());
            description.setText(restaurant.get("desc").toString());
            ratingBar.setRating(Float.valueOf(restaurant.get("rating").toString()));
            restID = restaurant.getString("id");
        } catch (JSONException e) {

        }

        try {
            Query query = databaseReference.child("ratings").child("restaurants").child(restID).orderByChild("date");

            FirebaseRecyclerOptions<RatingsClass> options =
                    new FirebaseRecyclerOptions.Builder<RatingsClass>()
                            .setQuery(query, new SnapshotParser<RatingsClass>() {
                                @NonNull
                                @Override
                                public RatingsClass parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    RatingsClass object = new RatingsClass(
                                            snapshot.child("name").getValue().toString(),
                                            Float.parseFloat(snapshot.child("rating").getValue().toString()),
                                            snapshot.child("comment").getValue().toString(),
                                            snapshot.child("date").getValue().toString().substring(0, 10)
                                    );
                                    return object;
                                }
                            })
                            .build();

            adapter = new FirebaseRecyclerAdapter<RatingsClass, RatingsHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull RatingsHolder holder, final int position, @NonNull RatingsClass model) {
                    holder.name.setText(model.getName());
                    holder.comment.setText(model.getComment());
                    holder.date.setText(model.getDate());
                    holder.rating.setRating(model.getValue());
                    if (model.getComment().equals("")) {
                        holder.comment.setVisibility(View.GONE);
                    }
                }

                @NonNull
                @Override
                public RatingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).
                            inflate(R.layout.reviews_listitem, viewGroup, false);
                    return new RatingsHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("MAD", "populate: ", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (Exception e) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (Exception e) {

        }
    }

    public interface DetailsInterface {
        void newRestaurantOrder(String restaurant);
    }
}
