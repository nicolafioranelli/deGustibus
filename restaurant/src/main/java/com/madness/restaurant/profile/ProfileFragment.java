package com.madness.restaurant.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        pref = this.getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
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

    /* Once the view is created, this method sets the title on the toolbar */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and add the title
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle(getResources().getString(R.string.title_Profile));
        return rootView;
    }

    /* All the items contained in the fragment are here populated with the stored values */
    @Override
    public void onResume() {
        fullname = getView().findViewById(R.id.tv_show_fullName);
        email = getView().findViewById(R.id.tv_show_email);
        desc = getView().findViewById(R.id.tv_show_desc);
        phone = getView().findViewById(R.id.tv_show_phone);
        address = getView().findViewById(R.id.tv_show_address);
        mondayOpen = getView().findViewById(R.id.tv_show_mondayOpen);
        mondayClose = getView().findViewById(R.id.tv_show_mondayClose);
        tuesdayOpen = getView().findViewById(R.id.tv_show_tuesdayOpen);
        tuesdayClose = getView().findViewById(R.id.tv_show_tuesdayClose);
        wednesdayOpen = getView().findViewById(R.id.tv_show_wednesdayOpen);
        wednesdayClose = getView().findViewById(R.id.tv_show_wednesdayClose);
        thursdayOpen = getView().findViewById(R.id.tv_show_thursdayOpen);
        thursdayClose = getView().findViewById(R.id.tv_show_thursdayClose);
        fridayOpen = getView().findViewById(R.id.tv_show_fridayOpen);
        fridayClose = getView().findViewById(R.id.tv_show_fridayClose);
        saturdayOpen = getView().findViewById(R.id.tv_show_saturdayOpen);
        saturdayClose = getView().findViewById(R.id.tv_show_saturdayClose);
        sundayOpen = getView().findViewById(R.id.tv_show_sundayOpen);
        sundayClose = getView().findViewById(R.id.tv_show_sundayClose);
        img = getView().findViewById(R.id.imageview);

        fullname.setText(pref.getString("name", getResources().getString(R.string.frProfile_defName)));
        email.setText(pref.getString("email", getResources().getString(R.string.frProfile_defEmail)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.frProfile_defDesc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.frProfile_defPhone)));
        address.setText(pref.getString("address", getResources().getString(R.string.frProfile_defAddress)));
        mondayOpen.setText(pref.getString("mondayOpen", getResources().getString(R.string.frProfile_defOpen)));
        mondayClose.setText(pref.getString("mondayClose", getResources().getString(R.string.frProfile_defClose)));
        tuesdayOpen.setText(pref.getString("tuesdayOpen", getResources().getString(R.string.frProfile_defOpen)));
        tuesdayClose.setText(pref.getString("tuesdayClose", getResources().getString(R.string.frProfile_defClose)));
        wednesdayOpen.setText(pref.getString("wednesdayOpen", getResources().getString(R.string.frProfile_defOpen)));
        wednesdayClose.setText(pref.getString("wednesdayClose", getResources().getString(R.string.frProfile_defClose)));
        thursdayOpen.setText(pref.getString("thursdayOpen", getResources().getString(R.string.frProfile_defOpen)));
        thursdayClose.setText(pref.getString("thursdayClose", getResources().getString(R.string.frProfile_defClose)));
        fridayOpen.setText(pref.getString("fridayOpen", getResources().getString(R.string.frProfile_defOpen)));
        fridayClose.setText(pref.getString("fridayClose", getResources().getString(R.string.frProfile_defClose)));
        saturdayOpen.setText(pref.getString("saturdayOpen", getResources().getString(R.string.frProfile_defOpen)));
        saturdayClose.setText(pref.getString("saturdayClose", getResources().getString(R.string.frProfile_defClose)));
        sundayOpen.setText(pref.getString("sundayOpen", getResources().getString(R.string.frProfile_defOpen)));
        sundayClose.setText(pref.getString("sundayClose", getResources().getString(R.string.frProfile_defClose)));
        if (pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
        super.onResume();
    }

    /* Interface for the listener */
    public interface ProfileListener {
        void onItemClicked();
    }

}
