package com.madness.deliveryman;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madness.deliveryman.notifications.NotificationsFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("deGustibus");
        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            final TextView userName = rootView.findViewById(R.id.welcomeName);
            databaseReference.child("riders").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        userName.setText(objectMap.get("name").toString());
                        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                        rootView.findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
                    } else {
                        rootView.findViewById(R.id.progress_horizontal).setVisibility(View.GONE);
                        rootView.findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return rootView;
    }

    /* Populates the menu with the notification button */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Add action to be performed once the item on the toolbar is clicked */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            Fragment fragment = null;
            Class fragmentClass;
            try {
                fragmentClass = NotificationsFragment.class;
                fragment = (Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Notifications").addToBackStack("HOME").commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}
