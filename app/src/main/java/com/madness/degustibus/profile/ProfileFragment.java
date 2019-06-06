package com.madness.degustibus.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.madness.degustibus.GlideApp;
import com.madness.degustibus.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    /* Views */
    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private TextView address;
    private CircleImageView img;
    private ProfileListener listener;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private DatabaseReference listenerReference;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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
        address = getView().findViewById(R.id.tv_show_address);
        img = getView().findViewById(R.id.imageview);

        loadFromFirebase();
    }

    private void loadFromFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        listenerReference = databaseReference.child("customers").child(user.getUid());

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
                    address.setText(user.get("address").toString());

                    String pic = null;
                    if (user.get("photo") != null) {
                        pic = user.get("photo").toString();
                    }
                    /* Glide */
                    GlideApp.with(getContext())
                            .load(pic)
                            .placeholder(R.drawable.user_profile)
                            .into(img);

                    getView().findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                    getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e("MAD", "onDataChange: ", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    /* Interface for the listener */
    public interface ProfileListener {
        void editProfileClick();
    }
}
