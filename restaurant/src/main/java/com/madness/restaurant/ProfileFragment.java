package com.madness.restaurant;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
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
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            //Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

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

        fullname.setText(pref.getString("name", getResources().getString(R.string.fullname)));
        email.setText(pref.getString("email", getResources().getString(R.string.email)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.desc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.phone)));
        address.setText(pref.getString("address", getResources().getString(R.string.address)));
        mondayOpen.setText(pref.getString("mondayOpen", getResources().getString(R.string.Opening)));
        mondayClose.setText(pref.getString("mondayClose", getResources().getString(R.string.Closing)));
        tuesdayOpen.setText(pref.getString("tuesdayOpen", getResources().getString(R.string.Opening)));
        tuesdayClose.setText(pref.getString("tuesdayClose", getResources().getString(R.string.Closing)));
        wednesdayOpen.setText(pref.getString("wednesdayOpen", getResources().getString(R.string.Opening)));
        wednesdayClose.setText(pref.getString("wednesdayClose", getResources().getString(R.string.Closing)));
        thursdayOpen.setText(pref.getString("thursdayOpen", getResources().getString(R.string.Opening)));
        thursdayClose.setText(pref.getString("thursdayClose", getResources().getString(R.string.Closing)));
        fridayOpen.setText(pref.getString("fridayOpen", getResources().getString(R.string.Opening)));
        fridayClose.setText(pref.getString("fridayClose", getResources().getString(R.string.Closing)));
        saturdayOpen.setText(pref.getString("saturdayOpen", getResources().getString(R.string.Opening)));
        saturdayClose.setText(pref.getString("saturdayClose", getResources().getString(R.string.Closing)));
        sundayOpen.setText(pref.getString("sundayOpen", getResources().getString(R.string.Opening)));
        sundayClose.setText(pref.getString("sundayClose", getResources().getString(R.string.Closing)));
        if(pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
        super.onResume();
    }
}
