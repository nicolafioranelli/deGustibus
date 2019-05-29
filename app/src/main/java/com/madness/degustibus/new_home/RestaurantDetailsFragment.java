package com.madness.degustibus.new_home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madness.degustibus.GlideApp;
import com.madness.degustibus.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantDetailsFragment extends Fragment {

    private ImageView imageView;
    private TextView title;
    private TextView description;
    private TextView address;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseRecyclerAdapter adapter;
    private JSONObject restaurant;
    private DetailsInterface detailsInterface;
    private Button button;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        imageView = rootView.findViewById(R.id.rest_imageView);
        title = rootView.findViewById(R.id.rest_title);
        address = rootView.findViewById(R.id.rest_subtitle);
        description = rootView.findViewById(R.id.rest_description);
        ratingBar = rootView.findViewById(R.id.ratingBar);
        button = rootView.findViewById(R.id.orderButton);
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

    private void populate(String restaurantProfileString) {
        try {
            restaurant = new JSONObject(restaurantProfileString);
            GlideApp.with(getContext())
                    .load(restaurant.get("photo").toString())
                    .placeholder(R.drawable.restaurant)
                    .into(imageView);

            title.setText(restaurant.get("name").toString());
            address.setText(restaurant.get("address").toString());
            description.setText(restaurant.get("desc").toString());
            ratingBar.setRating(Float.valueOf(restaurant.get("rating").toString()));
        } catch (JSONException e) {

        }

        /*
        Query query = databaseReference.child("ratings").child("restaurants").child(user.getUid()).orderByChild("date");

        FirebaseRecyclerOptions<RatingsClass> options =
                new FirebaseRecyclerOptions.Builder<RatingsClass>()
                        .setQuery(query, RatingsClass.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<RatingsClass, RatingsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RatingsHolder holder, final int position, @NonNull RatingsClass model) {
                holder.name.setText(model.getName());
                holder.comment.setText(model.getComment());
                holder.date.setText(model.getDate());
                holder.value.setText(model.getDate());
            }

            @NonNull
            @Override
            public RatingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.reviews_listitem, viewGroup, false);
                return new RatingsHolder(view);
            }
        };
        */
    }

    public interface DetailsInterface {
        void newRestaurantOrder(String restaurant);
    }
}
