package com.madness.restaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {

    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private EditText address;
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
    private int weekDay;
    private int moment;
    private ImageView img;
    private String cameraFilePath;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("DEGUSTIBUS", Context.MODE_PRIVATE);
        editor = pref.edit();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullname = getView().findViewById(R.id.et_edit_fullName);
        email = getView().findViewById(R.id.et_edit_email);
        desc = getView().findViewById(R.id.et_edit_desc);
        phone = getView().findViewById(R.id.et_edit_phone);
        address = getView().findViewById(R.id.et_edit_address);
        mondayOpen = getView().findViewById(R.id.et_edit_mondayOpen);
        mondayClose = getView().findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = getView().findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = getView().findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = getView().findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = getView().findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = getView().findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = getView().findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = getView().findViewById(R.id.et_edit_fridayOpen);
        fridayClose = getView().findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = getView().findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = getView().findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = getView().findViewById(R.id.et_edit_sundayOpen);
        sundayClose = getView().findViewById(R.id.et_edit_sundayClose);
        img = getView().findViewById(R.id.imageview);

        if(savedInstanceState != null){
            loadBundle(savedInstanceState);
        }else{
            loadSharedPrefs();
        }
    }

    /* Menu inflater for toolbar (adds elements inserted in res/menu/main_menu.xml */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /* Click listener to correctly handle actions related to toolbar items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Log.d("MAD", "onOptionsItemSelected: PRESSED");
            /* Define shared preferences and insert values */
            editor.putString("name", fullname.getText().toString());
            editor.putString("email", email.getText().toString());
            editor.putString("desc", desc.getText().toString());
            editor.putString("phone", phone.getText().toString());
            editor.putString("address", address.getText().toString());
            editor.putString("mondayOpen",mondayOpen.getText().toString());
            editor.putString("mondayClose",mondayClose.getText().toString());
            editor.putString("tuesdayOpen",tuesdayOpen.getText().toString());
            editor.putString("tuesdayClose",tuesdayClose.getText().toString());
            editor.putString("wednesdayOpen",wednesdayOpen.getText().toString());
            editor.putString("wednesdayClose",wednesdayClose.getText().toString());
            editor.putString("thursdayOpen",thursdayOpen.getText().toString());
            editor.putString("thursdayClose",thursdayClose.getText().toString());
            editor.putString("fridayOpen",fridayOpen.getText().toString());
            editor.putString("fridayClose",fridayClose.getText().toString());
            editor.putString("saturdayOpen",saturdayOpen.getText().toString());
            editor.putString("saturdayClose",saturdayClose.getText().toString());
            editor.putString("sundayOpen",sundayOpen.getText().toString());
            editor.putString("sundayClose",sundayClose.getText().toString());
            if (getPrefPhoto()!=null) {
                editor.putString("photo", getPrefPhoto());
            }
            editor.apply();
            delPrefPhoto();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStackImmediate("PROFILE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /* Methods (getters, setters and delete) to retrieve temporary photo uri. */
    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = this.getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photo", cameraFilePath);
        editor.commit();
    }

    private String getPrefPhoto() {
        SharedPreferences pref = this.getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photo", null);
    }

    private void delPrefPhoto() {
        SharedPreferences pref = this.getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photo");
        editor.apply();
    }

    private void loadSharedPrefs(){
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
        /* check if a photo is set */
        if (pref.getString("photo", null) != null) {
            img.setImageURI(Uri.parse(pref.getString("photo", null)));
        }
    }

    private void loadBundle(Bundle bundle){
        fullname.setText(bundle.getString("name"));
        email.setText(bundle.getString("email"));
        desc.setText(bundle.getString("desc"));
        phone.setText(bundle.getString("phone"));
        address.setText(bundle.getString("address"));
        mondayOpen.setText(bundle.getString("mondayOpen"));
        mondayClose.setText(bundle.getString("mondayClose"));
        tuesdayOpen.setText(bundle.getString("tuesdayOpen"));
        tuesdayClose.setText(bundle.getString("tuesdayClose"));
        tuesdayClose.setText(bundle.getString("tuesdayClose"));
        wednesdayOpen.setText(bundle.getString("wednesdayOpen"));
        wednesdayClose.setText(bundle.getString("wednesdayClose"));
        thursdayOpen.setText(bundle.getString("thursdayOpen"));
        thursdayClose.setText(bundle.getString("thursdayClose"));
        fridayOpen.setText(bundle.getString("fridayOpen"));
        fridayClose.setText(bundle.getString("fridayClose"));
        saturdayOpen.setText(bundle.getString("saturdayOpen"));
        saturdayClose.setText(bundle.getString("saturdayClose"));
        sundayOpen.setText(bundle.getString("sundayOpen"));
        sundayClose.setText(bundle.getString("sundayClose"));
        if(bundle.getString("photo")!=null) {
            img.setImageURI(Uri.parse(bundle.getString("photo")));
        }
    }

}
