package com.madness.degustibus.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madness.degustibus.R;

public class ProfileFragment extends Fragment {

    /* Views */
    private TextView fullname;
    private TextView email;
    private TextView desc;
    private TextView phone;
    private TextView address;
    private ImageView img;
    private SharedPreferences pref;
    private ProfileListener listener;

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
        pref = getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
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
    public void onResume() {
        super.onResume();
        fullname = getView().findViewById(R.id.tv_show_fullName);
        email = getView().findViewById(R.id.tv_show_email);
        desc = getView().findViewById(R.id.tv_show_desc);
        phone = getView().findViewById(R.id.tv_show_phone);
        address = getView().findViewById(R.id.tv_show_address);
        img = getView().findViewById(R.id.imageview);

        fullname.setText(pref.getString("name", getResources().getString(R.string.frProfile_defName)));
        email.setText(pref.getString("email", getResources().getString(R.string.frProfile_defEmail)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.frProfile_defDesc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.frProfile_defPhone)));
        address.setText(pref.getString("address", getResources().getString(R.string.frProfile_defAddr)));
        if (pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
    }

    /* Interface for the listener */
    public interface ProfileListener {
        void editProfileClick();
    }
}
