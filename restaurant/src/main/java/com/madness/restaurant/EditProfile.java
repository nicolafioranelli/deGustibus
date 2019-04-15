package com.madness.restaurant;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {

    private EditText fullname;
    private EditText email;
    private EditText desc;
    private EditText phone;
    private EditText address;
    private TextView defaultOpen;
    private TextView defaultClose;
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



    private EditPListener listener;

    public interface EditPListener {
        public void setEditPBarTitle(String title);
    }
    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof EditPListener) {
            listener = (EditPListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement EditPListner");
        }
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
        listener.setEditPBarTitle( getResources().getString(R.string.menu_editP));
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
        defaultOpen = getView().findViewById(R.id.et_edit_defaultOpen);
        defaultClose = getView().findViewById(R.id.et_edit_defaultClose);
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
            editor.putString("defaultOpen",defaultOpen.getText().toString());
            editor.putString("defaultClose",defaultClose.getText().toString());
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




    private void loadSharedPrefs(){
        fullname.setText(pref.getString("name", getResources().getString(R.string.fullname)));
        email.setText(pref.getString("email", getResources().getString(R.string.email)));
        desc.setText(pref.getString("desc", getResources().getString(R.string.desc)));
        phone.setText(pref.getString("phone", getResources().getString(R.string.phone)));
        address.setText(pref.getString("address", getResources().getString(R.string.address)));
        defaultOpen.setText(pref.getString("defaultOpen", getResources().getString(R.string.Opening)));
        defaultClose.setText(pref.getString("defaultClose", getResources().getString(R.string.Closing)));
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
        defaultOpen.setText(bundle.getString("defaultOpen"));
        defaultClose.setText(bundle.getString("defaultClose"));
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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fullname = getActivity().findViewById(R.id.et_edit_fullName);
        email = getActivity().findViewById(R.id.et_edit_email);
        desc = getActivity().findViewById(R.id.et_edit_desc);
        phone = getActivity().findViewById(R.id.et_edit_phone);
        address = getActivity().findViewById(R.id.et_edit_address);
        defaultOpen = getView().findViewById(R.id.et_edit_defaultOpen);
        defaultClose = getView().findViewById(R.id.et_edit_defaultClose);
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
        img = getActivity().findViewById(R.id.imageview);

        outState.putString("name",fullname.getText().toString());
        outState.putString("email",email.getText().toString());
        outState.putString("desc",desc.getText().toString());
        outState.putString("phone",phone.getText().toString());
        outState.putString("address",address.getText().toString());
        outState.putString("defaultOpen",defaultOpen.getText().toString());
        outState.putString("defaultClose",defaultClose.getText().toString());
        outState.putString("mondayOpen",mondayOpen.getText().toString());
        outState.putString("mondayClose",mondayClose.getText().toString());
        outState.putString("tuesdayOpen",tuesdayOpen.getText().toString());
        outState.putString("tuesdayClose",tuesdayClose.getText().toString());
        outState.putString("wednesdayOpen",wednesdayOpen.getText().toString());
        outState.putString("wednesdayClose",wednesdayClose.getText().toString());
        outState.putString("thursdayOpen",thursdayOpen.getText().toString());
        outState.putString("thursdayClose",thursdayClose.getText().toString());
        outState.putString("fridayOpen",fridayOpen.getText().toString());
        outState.putString("fridayClose",fridayClose.getText().toString());
        outState.putString("saturdayOpen",saturdayOpen.getText().toString());
        outState.putString("saturdayClose",saturdayClose.getText().toString());
        outState.putString("sundayOpen",sundayOpen.getText().toString());
        outState.putString("sundayClose",sundayClose.getText().toString());
        if(getPrefPhoto()==null) {
            outState.putString("photo", pref.getString("photo", null));
        } else {
            outState.putString("photo", getPrefPhoto());
        }
    }

    @Override
    public void onResume(){
        takeTimeTextViews();
        getPhoto();;
        super.onResume();
    }

    public void takeTimeTextViews(){
        LinearLayout layout1= (LinearLayout) getActivity().findViewById(R.id.defaultOpenLinearLayout);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=0;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout2= (LinearLayout) getActivity().findViewById(R.id.defaultCloseLinearLayout);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=0;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout3= (LinearLayout) getActivity().findViewById(R.id.mondayOpenLinearLayout);
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=1;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout4= (LinearLayout) getActivity().findViewById(R.id.mondayCloseLinearLayout);
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=1;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout5= (LinearLayout) getActivity().findViewById(R.id.tuesdayOpenLinearLayout);
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=2;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout6= (LinearLayout) getActivity().findViewById(R.id.tuesdayCloseLinearLayout);
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=2;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout7= (LinearLayout) getActivity().findViewById(R.id.wednesdayOpenLinearLayout);
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=3;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout8= (LinearLayout) getActivity().findViewById(R.id.wednesdayCloseLinearLayout);
        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=3;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout9= (LinearLayout) getActivity().findViewById(R.id.thursdayOpenLinearLayout);
        layout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=4;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout10= (LinearLayout) getActivity().findViewById(R.id.thursdayCloseLinearLayout);
        layout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=4;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout11= (LinearLayout) getActivity().findViewById(R.id.fridayOpenLinearLayout);
        layout11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=5;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout12= (LinearLayout) getActivity().findViewById(R.id.fridayCloseLinearLayout);
        layout12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=5;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout13= (LinearLayout) getActivity().findViewById(R.id.saturdayOpenLinearLayout);
        layout13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=6;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout14= (LinearLayout) getActivity().findViewById(R.id.saturdayCloseLinearLayout);
        layout14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=6;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout15= (LinearLayout) getActivity().findViewById(R.id.sundayOpenLinearLayout);
        layout15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=7;
                moment=0;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
        LinearLayout layout16= (LinearLayout) getActivity().findViewById(R.id.sundayCloseLinearLayout);
        layout16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay=7;
                moment=1;
                DialogFragment timePicker= new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");
            };
        });
    }

    public void setHourAndMinute(int hour, int minute) {
        mondayOpen = getActivity().findViewById(R.id.et_edit_mondayOpen);
        mondayClose = getActivity().findViewById(R.id.et_edit_mondayClose);
        tuesdayOpen = getActivity().findViewById(R.id.et_edit_tuesdayOpen);
        tuesdayClose = getActivity().findViewById(R.id.et_edit_tuesdayClose);
        wednesdayOpen = getActivity().findViewById(R.id.et_edit_wednesdayOpen);
        wednesdayClose = getActivity().findViewById(R.id.et_edit_wednesdayClose);
        thursdayOpen = getActivity().findViewById(R.id.et_edit_thursdayOpen);
        thursdayClose = getActivity().findViewById(R.id.et_edit_thursdayClose);
        fridayOpen = getActivity().findViewById(R.id.et_edit_fridayOpen);
        fridayClose = getActivity().findViewById(R.id.et_edit_fridayClose);
        saturdayOpen = getActivity().findViewById(R.id.et_edit_saturdayOpen);
        saturdayClose = getActivity().findViewById(R.id.et_edit_saturdayClose);
        sundayOpen = getActivity().findViewById(R.id.et_edit_sundayOpen);
        sundayClose = getActivity().findViewById(R.id.et_edit_sundayClose);
        switch (this.weekDay){
            case 0:
                if(this.moment==0) {
                    defaultOpen.setText(String.format("%02d:%02d", hour, minute));
                    mondayOpen.setText(String.format("%02d:%02d", hour, minute));
                    tuesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    wednesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    thursdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    fridayOpen.setText(String.format("%02d:%02d", hour, minute));
                    saturdayOpen.setText(String.format("%02d:%02d", hour, minute));
                    sundayOpen.setText(String.format("%02d:%02d", hour, minute));
                }
                else{
                    defaultClose.setText(String.format("%02d:%02d", hour, minute));
                    mondayClose.setText(String.format("%02d:%02d", hour, minute));
                    tuesdayClose.setText(String.format("%02d:%02d", hour, minute));
                    wednesdayClose.setText(String.format("%02d:%02d", hour, minute));
                    thursdayClose.setText(String.format("%02d:%02d", hour, minute));
                    fridayClose.setText(String.format("%02d:%02d", hour, minute));
                    saturdayClose.setText(String.format("%02d:%02d", hour, minute));
                    sundayClose.setText(String.format("%02d:%02d", hour, minute));
                }
                break;
            case 1:
                if(this.moment==0)
                    mondayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    mondayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 2:
                if(this.moment==0)
                    tuesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    tuesdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 3:
                if(this.moment==0)
                    wednesdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    wednesdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 4:
                if(this.moment==0)
                    thursdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    thursdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 5:
                if(this.moment==0)
                    fridayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    fridayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 6:
                if(this.moment==0)
                    saturdayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    saturdayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
            case 7:
                if(this.moment==0)
                    sundayOpen.setText(String.format("%02d:%02d", hour, minute));
                else
                    sundayClose.setText(String.format("%02d:%02d", hour, minute));
                break;
        }

    }public void getPhoto() {
        ImageView layout1 = (ImageView) getActivity().findViewById(R.id.imageview);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
                pictureDialog.setTitle(getActivity().getString(R.string.select_action));
                String[] pictureDialogItems = {
                        getResources().getText(R.string.camera).toString(), getResources().getText(R.string.gallery).toString()};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        checkCameraPermissions();
                                        /* Define image file where the camera will put the taken picture */
                                        File image;
                                        /* Get the directory to store image */
                                        File storageDir = getActivity().getApplicationContext().getFilesDir();
                                        try {
                                            image = File.createTempFile(
                                                    "img",
                                                    ".jpg",
                                                    storageDir
                                            );
                                            cameraFilePath = "file://" + image.getAbsolutePath();

                                            /* Set the Uri here before starting camera */
                                            setPrefPhoto(cameraFilePath);

                                            /* Start Intent for camera */
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", image));
                                            startActivityForResult(intent, 0);
                                        } catch (Exception e) {
                                            Log.e("MAD", "getPhoto: ", e);
                                        }

                                        break;
                                    case 1:
                                        //Create an Intent with action as ACTION_PICK
                                        Intent intent = new Intent(Intent.ACTION_PICK);
                                        // Sets the type as image/*. This ensures only components of type image are selected
                                        intent.setType("image/*");
                                        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                                        String[] mimeTypes = {"image/jpeg", "image/png"};
                                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                                        // Launching the Intent
                                        startActivityForResult(intent, 1);
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            };
        });
    }
    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Log.d("MAD", "onCreate: permission granted" );
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user captures an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Uri photo = Uri.parse(getPrefPhoto());
                    img.setImageURI(photo);
                    setPrefPhoto(photo.toString());
                    break;
                case 1:
                    Uri selectedImage = data.getData();
                    img.setImageURI(selectedImage);
                    setPrefPhoto(selectedImage.toString());
                    break;
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.d("MAD", "onActivityResult: CANCELED");
            try{
                File photoToCancel = new File(getPrefPhoto());
                photoToCancel.delete();
            } catch (Exception e) {
                Log.e("MAD", "onActivityResult: ", e);
            }
            delPrefPhoto();
        }
    }
    /* Methods (getters, setters and delete) to retrieve temporary photo uri. */
    private void setPrefPhoto(String cameraFilePath) {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("photo", cameraFilePath);
        editor.apply();
    }

    private String getPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return pref.getString("photo", null);
    }

    private void delPrefPhoto() {
        SharedPreferences pref = getActivity().getSharedPreferences("photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("photo");
        editor.apply();
    }
}
