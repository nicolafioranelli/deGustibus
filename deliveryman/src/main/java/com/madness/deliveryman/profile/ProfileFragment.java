package com.madness.deliveryman.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madness.deliveryman.GlideApp;
import com.madness.deliveryman.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    /* Views */
    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private RatingBar simpleRatingBar;
    private TextView vehicle;
    private Button reviews;
    private ImageView img;
    private ProfileListener listener;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private DatabaseReference listenerReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /* Define the listener here to manage clicks on the toolbar edit button */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileListener) {
            listener = (ProfileListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement ProfileListener");
        }
    }

    /* Sets the menu as available */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    /* Populates the menu with the edit button */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            listener.editProfileClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Once the view is created, this method sets the title on the toolbar */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);
        getActivity().setTitle(getString(R.string.title_Profile));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullname = getView().findViewById(R.id.tv_show_fullName);
        email = getView().findViewById(R.id.tv_show_email);
        desc = getView().findViewById(R.id.tv_show_desc);
        phone = getView().findViewById(R.id.tv_show_phone);
        vehicle = getView().findViewById(R.id.tv_show_vehicle);
        img = getView().findViewById(R.id.imageview);
        simpleRatingBar = getView().findViewById(R.id.simpleRatingBar);// initiate a rating bar
        simpleRatingBar.setNumStars(5);
        reviews = getView().findViewById(R.id.reviewsButton);

        loadFromFirebase();
        listenToButton();
    }

    private void listenToButton() {
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reviewsClick();
            }
        });
    }

    private void loadFromFirebase() {
        listenerReference = databaseReference.child("riders").child(user.getUid());

        eventListener = listenerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();

                    /* Load items of the view */
                    fullname.setText(user.get("name").toString());
                    email.setText(user.get("email").toString());
                    desc.setText(user.get("desc").toString());
                    phone.setText(user.get("phone").toString());
                    if(user.get("rating")!=null){
                        Float rating=Float.valueOf(user.get("rating").toString());
                        Float count=Float.valueOf(user.get("count").toString());
                        simpleRatingBar.setRating((rating/count));
                    }
                    else
                        simpleRatingBar.setRating(0);

                    String pic = null;
                    if (user.get("photo") != null) {
                        pic = user.get("photo").toString();
                    }
                    /* Glide */
                    GlideApp.with(getContext())
                            .load(pic)
                            .placeholder(R.drawable.user_profile)
                            .into(img);

                    String selector = user.get("vehicle").toString();
                    switch (selector) {
                        case "bike": {
                            String v = getString(R.string.bike);
                            vehicle.setText(v);
                        }
                        break;
                        case "car": {
                            String v = getString(R.string.car);
                            vehicle.setText(v);
                        }
                        break;
                        case "motorbike": {
                            String v = getString(R.string.motorbike);
                            vehicle.setText(v);
                        }
                        break;
                    }
                }catch (Exception e) {

                }
                getView().findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /* Interface for the listener */
    public interface ProfileListener {
        void editProfileClick();
        void reviewsClick();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listenerReference.removeEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerReference.removeEventListener(eventListener);
    }
}
