package com.madness.restaurant.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.restaurant.GlideApp;
import com.madness.restaurant.R;

/**
 * Profile Fragment class is used to manage the Restaurateur profile. The methods available
 * are used mainly to retrieve the shared preferences saved by the user. The switch to the
 * EditProfile fragment is managed through an Interface and directly by the HomeActivity.
 */
public class ProfileFragment extends Fragment {

    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private TextView address;
    private TextView mondayOpen;
    private TextView mondayClose;
    private TextView tuesdayOpen;
    private TextView tuesdayClose;
    private TextView wednesdayOpen;
    private TextView wednesdayClose;
    private TextView thursdayOpen;
    private TextView thursdayClose;
    private TextView fridayOpen;
    private TextView fridayClose;
    private TextView saturdayOpen;
    private TextView saturdayClose;
    private TextView sundayOpen;
    private TextView sundayClose;
    private ImageView img;
    private ProfileListener listener;
    private DatabaseReference databaseReference;
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setHasOptionsMenu(true);
    }

    /* Once the view is created, this method sets the title on the toolbar */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Profile));

        findViews(rootView);
        loadFromFirebase();
        return rootView;
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
            onClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Method to trigger the listener */
    public void onClick() {
        listener.onItemClicked();
    }

    private void findViews(View view) {
        fullname = view.findViewById(R.id.tv_show_fullName);
        email = view.findViewById(R.id.tv_show_email);
        desc = view.findViewById(R.id.tv_show_desc);
        phone = view.findViewById(R.id.tv_show_phone);
        address = view.findViewById(R.id.tv_show_address);
        mondayOpen = view.findViewById(R.id.tv_show_mondayOpen);
        mondayClose = view.findViewById(R.id.tv_show_mondayClose);
        tuesdayOpen = view.findViewById(R.id.tv_show_tuesdayOpen);
        tuesdayClose = view.findViewById(R.id.tv_show_tuesdayClose);
        wednesdayOpen = view.findViewById(R.id.tv_show_wednesdayOpen);
        wednesdayClose = view.findViewById(R.id.tv_show_wednesdayClose);
        thursdayOpen = view.findViewById(R.id.tv_show_thursdayOpen);
        thursdayClose = view.findViewById(R.id.tv_show_thursdayClose);
        fridayOpen = view.findViewById(R.id.tv_show_fridayOpen);
        fridayClose = view.findViewById(R.id.tv_show_fridayClose);
        saturdayOpen = view.findViewById(R.id.tv_show_saturdayOpen);
        saturdayClose = view.findViewById(R.id.tv_show_saturdayClose);
        sundayOpen = view.findViewById(R.id.tv_show_sundayOpen);
        sundayClose = view.findViewById(R.id.tv_show_sundayClose);
        img = view.findViewById(R.id.imageview);
    }

    private void loadFromFirebase() {
        databaseReference.child("restaurants").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ProfileClass profile = dataSnapshot.getValue(ProfileClass.class);

                    fullname.setText(profile.getName());
                    email.setText(profile.getEmail());
                    desc.setText(profile.getDesc());
                    phone.setText(profile.getPhone());
                    address.setText(profile.getAddress());
                    mondayOpen.setText(profile.getMondayOpen());
                    mondayClose.setText(profile.getMondayClose());
                    tuesdayOpen.setText(profile.getTuesdayOpen());
                    tuesdayClose.setText(profile.getTuesdayClose());
                    wednesdayOpen.setText(profile.getWednesdayOpen());
                    wednesdayClose.setText(profile.getWednesdayClose());
                    thursdayOpen.setText(profile.getThursdayOpen());
                    thursdayClose.setText(profile.getThursdayClose());
                    fridayOpen.setText(profile.getFridayOpen());
                    fridayClose.setText(profile.getFridayClose());
                    saturdayOpen.setText(profile.getSaturdayOpen());
                    saturdayClose.setText(profile.getSaturdayClose());
                    sundayOpen.setText(profile.getSundayOpen());
                    sundayClose.setText(profile.getSundayClose());

                    String pic = null;
                    if (profile.getPhoto() != null) {
                        pic = profile.getPhoto();
                    }
                    /* Glide */
                    GlideApp.with(getContext())
                            .load(pic)
                            .placeholder(R.drawable.user_profile)
                            .into(img);
                } else {
                    String pic = null;
                    GlideApp.with(getContext())
                            .load(pic)
                            .placeholder(R.drawable.user_profile)
                            .into(img);

                    email.setText(user.getEmail());
                    mondayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    mondayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    tuesdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    tuesdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    wednesdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    wednesdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    thursdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    thursdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    fridayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    fridayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    saturdayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    saturdayClose.setText(getResources().getString(R.string.frProfile_defClose));
                    sundayOpen.setText(getResources().getString(R.string.frProfile_defOpen));
                    sundayClose.setText(getResources().getString(R.string.frProfile_defClose));
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
        void onItemClicked();
    }

}
